package com.onedigit.utah.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;

@ConfigurationProperties(prefix = "coin.cache")
public record CacheProperties (
        @DefaultValue List<String> includedTickers,
        String mode,
        int scheduledIntervalMs
) {
    public boolean isLive() {
        return mode == null || "live".equalsIgnoreCase(mode);
    }
}
