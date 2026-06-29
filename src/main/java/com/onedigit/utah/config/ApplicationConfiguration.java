package com.onedigit.utah.config;

import com.onedigit.utah.integration.common.RestErrorLoggingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import static com.onedigit.utah.constants.ApiConstants.*;


@Configuration
public class ApplicationConfiguration {

    private final RestErrorLoggingFilter errorLoggingFilter;
    final int frameSize = 16 * 1024 * 1024;

    public ApplicationConfiguration(RestErrorLoggingFilter errorLoggingFilter) {
        this.errorLoggingFilter = errorLoggingFilter;
    }

    private WebClient restClient(String baseUrl) {
        return WebClient.builder()
                .filter(errorLoggingFilter)
                .codecs(c -> c.defaultCodecs().maxInMemorySize(frameSize))
                .baseUrl(baseUrl)
                .build();
    }

    @Bean
    public WebClient kucoinRestApiClient() {
        return restClient(KUCOIN_API_REST_BASE_URL);
    }

    @Bean
    public WebClient mexcRestApiClient() {
        return restClient(MEXC_API_REST_BASE_URL);
    }

    @Bean
    public WebClient bybitRestApiClient() {
        return restClient(BYBIT_API_REST_BASE_URL);
    }

    @Bean
    public WebSocketClient webSocketClient() {
        return new ReactorNettyWebSocketClient();
    }
}
