package com.onedigit.utah.service.event.publishers;

import com.onedigit.utah.service.CoinViewAssembler;
import com.onedigit.utah.service.MarketLocalCache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


/**
 * Scheduled mode: fills the sink on a fixed timer (one tick drives both outputs).
 */
@Service
@ConditionalOnProperty(prefix = "coin.cache", name = "mode", havingValue = "scheduled")
public class ScheduledPublisher extends CoinViewPublisher {

    private final MarketLocalCache cache;
    private final CoinViewAssembler assembler;

    public ScheduledPublisher(MarketLocalCache cache, CoinViewAssembler assembler) {
        this.cache = cache;
        this.assembler = assembler;
    }

    @Scheduled(fixedDelayString = "${coin.cache.scheduled-interval-ms:1000}")
    void sweep() {
        cache.snapshot().stream().map(assembler::verbose).forEach(this::publish);
    }
}
