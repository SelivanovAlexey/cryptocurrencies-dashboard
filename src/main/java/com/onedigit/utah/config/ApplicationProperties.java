package com.onedigit.utah.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;

@ConfigurationProperties(prefix = "app")
public record ApplicationProperties(
        int healthcheckIntervalMs,
        int healthcheckStaleThresholdMs
) {}
