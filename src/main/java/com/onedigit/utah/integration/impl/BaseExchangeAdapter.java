package com.onedigit.utah.integration.impl;

import com.onedigit.utah.integration.ExchangeAdapter;
import com.onedigit.utah.integration.common.RestApiAdapter;
import com.onedigit.utah.model.Connection;
import com.onedigit.utah.model2.api.SpreadDTO;
import com.onedigit.utah.model2.integration.common.RestResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.retry.Repeat;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;

import static com.onedigit.utah.constants.ApiConstants.REST_API_CALLS_FREQUENCY_MS;


@Slf4j
public abstract class BaseExchangeAdapter extends RestApiAdapter implements ExchangeAdapter {
    @Getter
    @Setter
    protected Connection connectionStatus = Connection.INACTIVE;

    private final Repeat<Object> repeatStrategy = Repeat.times(Long.MAX_VALUE).fixedBackoff(Duration.ofMillis(REST_API_CALLS_FREQUENCY_MS));
    private final Retry retryStrategy =
            Retry.backoff(Long.MAX_VALUE, Duration.ofSeconds(5))
                    .doBeforeRetry(signal -> {
                        setConnectionStatus(Connection.INACTIVE);
                        log.error("Unexpected error from API - {}\nResponse body: {}\nRetrying...",
                                signal.failure().getMessage(),
                                signal.failure() instanceof WebClientResponseException ex ? ex.getResponseBodyAsString() : "null",
                                signal.failure());
                    });

    @Override
    public Flux<List<List<SpreadDTO>>> getSpreadsFlux() {
        return getPrices()
                .map(this::populateSpreads)
                .repeatWhen(repeatStrategy)
                .retryWhen(retryStrategy);
    }

    @Override
    public Flux<? extends RestResponse> getAndPopulateAvailability() {
        return getAvailability()
                .map(this::populateAvailability)
                .repeatWhen(repeatStrategy)
                .retryWhen(retryStrategy);
    }

    protected abstract Mono<? extends RestResponse> getPrices();

    protected abstract Mono<? extends RestResponse> getAvailability();

    protected abstract List<List<SpreadDTO>> populateSpreads(RestResponse response);

    protected abstract RestResponse populateAvailability(RestResponse response);
}
