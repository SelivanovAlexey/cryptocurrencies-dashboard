package com.onedigit.utah.integration.impl;

import com.onedigit.utah.integration.ExchangeAdapter;
import com.onedigit.utah.integration.common.RestApiAdapter;
import com.onedigit.utah.model.Connection;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;

public abstract class BaseExchangeAdapter extends RestApiAdapter implements ExchangeAdapter {
    @Getter
    @Setter
    protected Connection connectionStatus = Connection.INACTIVE;

    protected Retry exchangeApiRetrySpec(Logger log) {
        return Retry
                .backoff(Long.MAX_VALUE, Duration.ofSeconds(5))
                .doBeforeRetry(signal -> {
                    setConnectionStatus(Connection.INACTIVE);
                    log.error("Unexpected error from API - {}\nResponse body: {}.\nRetrying.",
                            signal.failure().getMessage(),
                            ((WebClientResponseException) signal.failure()).getResponseBodyAsString(),
                            signal.failure());
                });
    }
}
