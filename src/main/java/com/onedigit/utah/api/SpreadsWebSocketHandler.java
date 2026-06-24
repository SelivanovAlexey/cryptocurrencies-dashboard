package com.onedigit.utah.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.WebSocketMessage;
import reactor.core.publisher.Mono;

//TODO: make abstract websockethandler with json conversion, errorhandling, etc...
@Service
public class SpreadsWebSocketHandler implements WebSocketHandler {

    private final FluxFacade fluxFacade;
    private final ObjectMapper mapper;

    public SpreadsWebSocketHandler(FluxFacade fluxFacade, ObjectMapper mapper) {
        this.fluxFacade = fluxFacade;
        this.mapper = mapper;
    }

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        return webSocketSession.send(fluxFacade.getSpreadsFlux()
                        .map(payload -> webSocketSession.textMessage(toJson(payload))))
                .and(webSocketSession.receive()
                        .map(WebSocketMessage::getPayloadAsText)
                        .log());
    }
    @SneakyThrows
    private String toJson(Object value){
       return mapper.writeValueAsString(value);
    }
}
