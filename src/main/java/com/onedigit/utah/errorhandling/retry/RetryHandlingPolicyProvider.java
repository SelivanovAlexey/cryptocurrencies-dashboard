package com.onedigit.utah.errorhandling.retry;

import com.onedigit.utah.config.RestProperties;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.function.Consumer;

@Component
public class RetryHandlingPolicyProvider {

    private final RestProperties restProperties;
    private volatile boolean shuttingDown = false;

    public RetryHandlingPolicyProvider(RestProperties restProperties) {
        this.restProperties = restProperties;
    }

    public Retry backoffRetryPolicy(Consumer<Retry.RetrySignal> consumer) {
        return Retry.backoff(Long.MAX_VALUE, Duration.ofSeconds(restProperties.retryBackoffSeconds()))
                .maxBackoff(Duration.ofSeconds(30))
                .transientErrors(true)
                .doBeforeRetry(signal -> {
                    if (!shuttingDown) consumer.accept(signal);
                });
    }

    @EventListener(ContextClosedEvent.class)
    void onShutdown() {
        shuttingDown = true;
    }
}
