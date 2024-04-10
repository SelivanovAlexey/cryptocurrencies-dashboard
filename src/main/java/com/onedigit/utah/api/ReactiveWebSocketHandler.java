package com.onedigit.utah.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.WebSocketMessage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static java.time.LocalTime.now;
import static java.util.UUID.randomUUID;

@Service
public class ReactiveWebSocketHandler implements WebSocketHandler {

    private final ObjectMapper json;

    public ReactiveWebSocketHandler(ObjectMapper json) {
        this.json = json;
    }

    private Flux<String> eventFlux() {
        return Flux.generate(sink -> {
            Event event = new Event(randomUUID().toString(), now().toString());
            try {
                sink.next(json.writeValueAsString(event));
            } catch (JsonProcessingException e) {
                sink.error(e);
            }
        });
    }

    private Flux<String> intervalFlux = Flux.interval(Duration.ofMillis(1000L))
            .zipWith(eventFlux(), (time, event) -> event);


    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        return webSocketSession.send(intervalFlux
                        .map(webSocketSession::textMessage))
                .and(webSocketSession.receive()
                        .map(WebSocketMessage::getPayloadAsText)
                        .log());
    }

    @Data
    @AllArgsConstructor
    class Event {
        private String eventId;
        private String eventDt;
    }
}
