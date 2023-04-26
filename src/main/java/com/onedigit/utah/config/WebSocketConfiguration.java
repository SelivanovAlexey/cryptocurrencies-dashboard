package com.onedigit.utah.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import java.util.Map;

@Configuration
public class WebSocketConfiguration {
    private final WebSocketHandler handler;

    public WebSocketConfiguration(WebSocketHandler handler) {
        this.handler = handler;
    }

    @Bean
    public HandlerMapping handlerMapping(){
        Map<String, WebSocketHandler> handlerMap = Map.of(
                "/ws", handler
        );
        return new SimpleUrlHandlerMapping(handlerMap, 1);
    }

    @Bean
    public WebSocketHandlerAdapter webSocketHandlerAdapter() {
        return new WebSocketHandlerAdapter();
    }

}
