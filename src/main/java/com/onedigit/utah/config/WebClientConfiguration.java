package com.onedigit.utah.config;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.BodyExtractor;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import static com.onedigit.utah.constants.ApiConstants.BYBIT_API_BASE_URL;
import static com.onedigit.utah.constants.ApiConstants.KUCOIN_API_BASE_URL;


@Configuration
@Slf4j
public class WebClientConfiguration {

    final int size = 16 * 1024 * 1024;
    final ExchangeStrategies strategy = ExchangeStrategies.builder()
            .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
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
    public WebSocketClient kucoinWsApiClient() {
        return new ReactorNettyWebSocketClient();
    }


}
