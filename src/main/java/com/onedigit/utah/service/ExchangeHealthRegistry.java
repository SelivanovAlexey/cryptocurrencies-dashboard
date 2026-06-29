package com.onedigit.utah.service;

import com.onedigit.utah.model.Exchange;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ExchangeHealthRegistry {

    private final Map<Exchange, Instant> lastSuccess = new ConcurrentHashMap<>();

    public void recordSuccess(Exchange exchange) {
        lastSuccess.put(exchange, Instant.now());
    }

    public Instant lastSuccess(Exchange exchange) {
        return lastSuccess.get(exchange);
    }
}