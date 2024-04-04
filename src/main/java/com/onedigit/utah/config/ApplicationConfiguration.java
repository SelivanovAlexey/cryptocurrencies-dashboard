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
