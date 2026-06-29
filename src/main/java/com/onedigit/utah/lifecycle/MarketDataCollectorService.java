package com.onedigit.utah.lifecycle;

import com.onedigit.utah.errorhandling.retry.RetryHandlingPolicyProvider;
import com.onedigit.utah.integration.ExchangeAdapter;
import com.onedigit.utah.model.Exchange;
import com.onedigit.utah.model.api.CoinView;
import com.onedigit.utah.service.CoinViewAssembler;
import com.onedigit.utah.service.ExchangeHealthRegistry;
import com.onedigit.utah.service.FeedSampleLogger;
import com.onedigit.utah.service.MarketLocalCache;
import com.onedigit.utah.service.event.publishers.CoinViewPublisher;
import com.onedigit.utah.service.event.publishers.LivePublisher;
import com.onedigit.utah.service.event.publishers.Publisher;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.Disposables;
import reactor.core.scheduler.Scheduler;
import reactor.util.retry.Retry;

import java.util.List;

@Service
@Slf4j
public class MarketDataCollectorService {

    private final List<ExchangeAdapter> adapters;
    private final MarketLocalCache cache;
    private final CoinViewAssembler assembler;
    private final Publisher<CoinView> pub;
    private final Scheduler spreadScheduler;
    private final ExchangeHealthRegistry health;
    private final boolean liveCacheMode;
    private final Disposable.Composite subscriptions = Disposables.composite();
    private final RetryHandlingPolicyProvider policyProvider;
    private final FeedSampleLogger sampleLogger;


    public MarketDataCollectorService(List<ExchangeAdapter> adapters, MarketLocalCache cache,
                                      CoinViewAssembler assembler, CoinViewPublisher pub, Scheduler spreadScheduler,
                                      ExchangeHealthRegistry health, RetryHandlingPolicyProvider policyProvider,
                                      FeedSampleLogger sampleLogger) {
        this.adapters = adapters;
        this.cache = cache;
        this.assembler = assembler;
        this.pub = pub;
        this.liveCacheMode = pub instanceof LivePublisher;
        this.spreadScheduler = spreadScheduler;
        this.health = health;
        this.policyProvider = policyProvider;
        this.sampleLogger = sampleLogger;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void prices() {
        adapters.forEach(a -> {
            log.info("Wiring {} feed (prices)", a.exchange());
            subscriptions.add(a.priceUpdates()
                    .onBackpressureLatest()
                    .publishOn(spreadScheduler)
                    .doOnNext(batch -> {
                        long t0 = System.nanoTime();
                        batch.forEach(p -> {
                            if (cache.update(p) && liveCacheMode) emit(p.ticker());
                        });
                        log.info("{} prices process {} us", a.exchange(), (System.nanoTime() - t0) / 1_000);
                        health.recordSuccess(a.exchange());
                        sampleLogger.prices(a.exchange(), batch);
                    })
                    .retryWhen(feedRestartPolicy("prices", a.exchange()))
                    .subscribe(v -> {
                    }, e -> log.error("Feed {}:prices stopped permanently", a.exchange(), e)));
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    public void availability() {
        adapters.forEach(a -> {
            log.info("Wiring {} feed (availability)", a.exchange());
            subscriptions.add(a.availabilityUpdates()
                    .onBackpressureLatest()
                    .publishOn(spreadScheduler)
                    .doOnNext(batch -> {
                        long t0 = System.nanoTime();
                        batch.forEach(au -> {
                            if (cache.update(au) && liveCacheMode) emit(au.ticker());
                        });
                        log.info("{} availability process {} us", a.exchange(), (System.nanoTime() - t0) / 1_000);
                        health.recordSuccess(a.exchange());
                        sampleLogger.availability(a.exchange(), batch);
                    })
                    .retryWhen(feedRestartPolicy("availability", a.exchange()))
                    .subscribe(v -> {
                    }, e -> log.error("Feed {}:availability stopped permanently", a.exchange(), e)));
        });
    }

    private void emit(String ticker) {
        cache.find(ticker).map(assembler::verbose).ifPresent(pub::publish);
    }

    private Retry feedRestartPolicy(String feedName, Exchange exchange) {
        return policyProvider.backoffRetryPolicy(s -> log.warn("Restarting {}:{} feed: {}", exchange, feedName, s.failure().toString()));
    }

    @PreDestroy
    public void shutdown() {
        subscriptions.dispose();
        log.info("MarketDataCollectorService gathering stopped");
    }


}
