package com.onedigit.utah.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.netty.http.client.HttpClient;

import static com.onedigit.utah.constants.ApiConstants.*;


@Configuration
public class WebClientConfiguration {
    final int frameSize = 16 * 1024 * 1024;
    final ExchangeStrategies strategy = ExchangeStrategies.builder()
            .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(frameSize))
            .build();
    @Bean
    public WebClient kucoinRestApiClient(WebClient.Builder builder) {
        return builder
                .exchangeStrategies(strategy)
                .baseUrl(KUCOIN_API_REST_BASE_URL).build();
    }

    @Bean
    public WebClient mexcRestApiClient(WebClient.Builder builder) {
        return builder
                .exchangeStrategies(strategy)
                .baseUrl(MEXC_API_REST_BASE_URL).build();
    }
    @Bean
    public WebClient bybitRestApiClient(WebClient.Builder builder) {
        return builder
                .exchangeStrategies(strategy)
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create().wiretap(true)
                ))
                .baseUrl(BYBIT_API_REST_BASE_URL).build();
    }
    @Bean
    public WebSocketClient webSocketClient() {
        return new ReactorNettyWebSocketClient();
    }


}
