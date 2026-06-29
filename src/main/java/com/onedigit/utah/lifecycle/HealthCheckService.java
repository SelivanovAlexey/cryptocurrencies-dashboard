package com.onedigit.utah.lifecycle;

import com.onedigit.utah.config.ApplicationProperties;
import com.onedigit.utah.integration.ExchangeAdapter;
import com.onedigit.utah.model.Connection;
import com.onedigit.utah.model.Exchange;
import com.onedigit.utah.service.ExchangeHealthRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class HealthCheckService {
    private final ApplicationProperties applicationProperties;
    private final List<ExchangeAdapter> activeAdapters;
    private final ExchangeHealthRegistry health;

    private final Map<Exchange, Connection> lastReported = new ConcurrentHashMap<>();

    public HealthCheckService(ApplicationProperties applicationProperties, List<ExchangeAdapter> adapters, ExchangeHealthRegistry health) {
        this.applicationProperties = applicationProperties;
        this.activeAdapters = adapters;
        this.health = health;
//        adapters.forEach(a -> lastReported.put(a.exchange(), Connection.inactive));
    }

    @Scheduled(fixedDelayString = "${app.healthcheck-interval-ms:60000}", scheduler = "healthCheckScheduler")
    public void checkHealth() {
        Instant now = Instant.now();
        long staleThresholdMs = applicationProperties.healthcheckStaleThresholdMs();

        activeAdapters.forEach(adapter -> {
            Exchange exchange = adapter.exchange();
            Instant last = health.lastSuccess(exchange);
            Connection current = isFresh(last, now, staleThresholdMs) ? Connection.active : Connection.inactive;

            Connection previous = lastReported.put(exchange, current);

            if (current != previous) {
                if (current == Connection.active) {
                    log.info("Healthcheck :: {} :: active", exchange);
                } else {
                    log.warn("Healthcheck :: {} :: inactive (no data > {} ms)", exchange, staleThresholdMs);
                }
            }
        });
    }
    private boolean isFresh(Instant last, Instant now, long thresholdMs) {
        return last != null && Duration.between(last, now).toMillis() < thresholdMs;
    }
}