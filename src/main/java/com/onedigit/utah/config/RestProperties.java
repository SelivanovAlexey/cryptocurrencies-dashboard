package com.onedigit.utah.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rest")
public record RestProperties(
        int retryBackoffSeconds,
        int priceCallsIntervalMs,
        int availabilityCallsIntervalMs
) {}
