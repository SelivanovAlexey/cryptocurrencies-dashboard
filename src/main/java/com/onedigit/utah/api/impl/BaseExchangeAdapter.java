package com.onedigit.utah.api.impl;

import com.onedigit.utah.api.ExchangeAdapter;
import com.onedigit.utah.api.common.RestApiAdapter;
import com.onedigit.utah.model.Connection;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
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
                    log.error("Unexpected error from API: {}. Retrying.", signal.failure().getMessage(), signal.failure());
                });
    }
}
