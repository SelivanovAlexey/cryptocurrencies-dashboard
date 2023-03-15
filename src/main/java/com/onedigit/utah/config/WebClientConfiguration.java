package com.onedigit.utah.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.netty.http.client.HttpClient;

import static com.onedigit.utah.constants.ApiConstants.BYBIT_API_BASE_URL;
import static com.onedigit.utah.constants.ApiConstants.KUCOIN_API_BASE_URL;


@Configuration
public class WebClientConfiguration {
    final int frameSize = 16 * 1024 * 1024;
    final ExchangeStrategies strategy = ExchangeStrategies.builder()
            .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(frameSize))
            .build();
    @Bean
    public WebClient kucoinApiClient(WebClient.Builder builder) {
        return builder
                .exchangeStrategies(strategy)
                .baseUrl(KUCOIN_API_BASE_URL).build();
    }
    @Bean
    public WebClient bybitApiClient(WebClient.Builder builder) {
        return builder
                .exchangeStrategies(strategy)
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create().wiretap(true)
                ))
                .baseUrl(BYBIT_API_BASE_URL).build();
    }
    @Bean
    public WebSocketClient webSocketClient() {
        return new ReactorNettyWebSocketClient();
    }


}
