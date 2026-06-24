package com.onedigit.utah.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class WebsocketConfiguration {
    private final WebSocketHandler spreadsWebSocketHandler;
    private final WebSocketHandler chainsWebSocketHandler;

    public WebsocketConfiguration(WebSocketHandler spreadsWebSocketHandler, WebSocketHandler chainsWebSocketHandler) {
        this.spreadsWebSocketHandler = spreadsWebSocketHandler;
        this.chainsWebSocketHandler = chainsWebSocketHandler;
    }

    @Bean
    public HandlerMapping webSocketHandlerMapping() {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/spreads", spreadsWebSocketHandler);
        map.put("/chains", chainsWebSocketHandler);

        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setOrder(1);
        handlerMapping.setUrlMap(map);
        return handlerMapping;
    }
}
