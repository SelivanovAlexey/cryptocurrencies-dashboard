package com.onedigit.utah.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "integration")
@Validated
public record IntegrationProperties(
        @NotBlank String bybitApikeyValue,
        @NotBlank String bybitApiKeySecret,
        @NotBlank String mexcApiKeyValue,
        @NotBlank String mexcApiKeySecret
) {}
