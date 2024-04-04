package com.onedigit.utah.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import static com.onedigit.utah.constants.ApiConstants.*;

@Configuration
public class IntegrationConfiguration {

    @Component
    public static class CodecsSizeCustomizer implements WebClientCustomizer {
        final int frameSize = 16 * 1024 * 1024;
        final ExchangeStrategies strategy = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(frameSize))
                .build();
        @Override
        public void customize(WebClient.Builder webClientBuilder) {
            webClientBuilder.exchangeStrategies(strategy);
        }
    }

    @Bean
    @Qualifier("kucoinRestApiClient")
    public WebClient kucoinRestApiClient(WebClient.Builder baseWebClientBuilder) {
        return baseWebClientBuilder
                .baseUrl(KUCOIN_API_REST_BASE_URL).build();
    }

    @Bean
    @Qualifier("mexcRestApiClient")
    public WebClient mexcRestApiClient(WebClient.Builder baseWebClientBuilder) {
        return baseWebClientBuilder
                .baseUrl(MEXC_API_REST_BASE_URL).build();
    }
    @Bean
    @Qualifier("bybitRestApiClient")
    public WebClient bybitRestApiClient(WebClient.Builder baseWebClientBuilder) {
        return baseWebClientBuilder
                .baseUrl(BYBIT_API_REST_BASE_URL).build();
    }
}
