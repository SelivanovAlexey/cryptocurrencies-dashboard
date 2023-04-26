package com.onedigit.utah.controllers;


import com.onedigit.utah.model.CoinDTO;
import com.onedigit.utah.service.MarketLocalCache;
import com.onedigit.utah.service.event.PriceChangeEventProcessor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//TODO: low prio - research with rsocket support
@Service
public class WebSocketController implements WebSocketHandler {

    private final PriceChangeEventProcessor eventProcessor;

    public WebSocketController(PriceChangeEventProcessor eventProcessor) {
        this.eventProcessor = eventProcessor;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
//        return session.send(session.receive().flatMap(msg -> getSpreadsUpdateInitial().map(data -> session.textMessage(data.toString()))));
        return session.receive().flatMap(wsMessage -> routeMessage(wsMessage, session)).then();
    }

    private Mono<Void> routeMessage(WebSocketMessage webSocketMessage, WebSocketSession session) {
//        return session.send(getSpreadsUpdateInitial().map(data -> session.textMessage(data.toString())))
          return getSpreadsUpdate().map(coinDto -> session.send(Mono.just(session.textMessage(coinDto.toString())))).then()
                ;
    }

    private Flux<CoinDTO> getSpreadsUpdate() {
        Flux<CoinDTO> mainFlow = Flux.create(sink ->
                eventProcessor.register(sink::next));

        return mainFlow;
    }

    private Flux<CoinDTO> getSpreadsUpdateInitial() {
        return Mono.just(MarketLocalCache.getAllExchangesData())
                .flatMapMany(Flux::fromIterable);
    }
}
