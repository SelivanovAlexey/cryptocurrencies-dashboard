package com.onedigit.utah.integration.impl;

import com.onedigit.utah.integration.ExchangeAdapter;
import com.onedigit.utah.integration.common.RestApiAdapter;
import com.onedigit.utah.errorhandling.retry.RetryHandlingPolicyProvider;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.util.retry.Retry;

import java.util.List;

public abstract class BaseExchangeAdapter extends RestApiAdapter implements ExchangeAdapter {

    @Autowired
    protected RetryHandlingPolicyProvider retryPolicy;

    protected Retry exchangeRetryPolicy(Logger log) {
        return retryPolicy.backoffRetryPolicy(signal ->
                log.warn("API error from {}: {}. Retrying.", exchange(), signal.failure().getMessage(), signal.failure()));
    }

    protected <T, K> List<T> logParseErrors(Logger log, K response) {
        log.warn("{} failed to parse response: {}", exchange(), response);
        return List.of();
    }
}
