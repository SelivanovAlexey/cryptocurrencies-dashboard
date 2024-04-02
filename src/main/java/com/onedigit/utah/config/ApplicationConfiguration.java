package com.onedigit.utah.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;


import static com.onedigit.utah.constants.ApiConstants.*;

@Configuration
public class ApplicationConfiguration {

    final int frameSize = 16 * 1024 * 1024;
    final ExchangeStrategies strategy = ExchangeStrategies.builder()
            .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(frameSize))
            .build();

    private final WebClient.Builder autoConfiguredWebClientBuilder;

    public ApplicationConfiguration(@Autowired WebClient.Builder autoConfiguredWebClientBuilder) {
        this.autoConfiguredWebClientBuilder = autoConfiguredWebClientBuilder;
    }

    @Bean
    @Qualifier("baseWebClientBuilder")
    public WebClient.Builder baseWebClientBuilder() {
        return autoConfiguredWebClientBuilder
                .exchangeStrategies(strategy);
    }

    @Bean
    @Qualifier("kucoinRestApiClient")
    public WebClient kucoinRestApiClient(@Qualifier("baseWebClientBuilder") WebClient.Builder baseWebClientBuilder) {
        return baseWebClientBuilder
                .baseUrl(KUCOIN_API_REST_BASE_URL).build();
    }

    @Bean
    @Qualifier("mexcRestApiClient")
    public WebClient mexcRestApiClient(@Qualifier("baseWebClientBuilder") WebClient.Builder baseWebClientBuilder) {
        return baseWebClientBuilder
                .baseUrl(MEXC_API_REST_BASE_URL).build();
    }
    @Bean
    @Qualifier("bybitRestApiClient")
    public WebClient bybitRestApiClient(@Qualifier("baseWebClientBuilder") WebClient.Builder baseWebClientBuilder) {
        return baseWebClientBuilder
                .baseUrl(BYBIT_API_REST_BASE_URL).build();
    }
    @Bean
    public WebSocketClient webSocketClient() {
        return new ReactorNettyWebSocketClient();
    }

//    @Bean
//    public Map<Exchange, ExchangeAdapter> availableAdaptersProvider(List<BaseExchangeAdapter> adapters){
//        return adapters.stream()
//                .filter(ExchangeAdapter::isEnabled)
//                .collect(Collectors.toMap(ExchangeAdapter::getExchangeName, Function.identity()));
//    }

}
