package com.onedigit.utah.controllers;

import com.onedigit.utah.model.event.CoinUpdateDTO;
import com.onedigit.utah.service.event.PriceChangeEventProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


@RequestMapping("/api")
@Slf4j
@RestController
public class Controller {
    private final PriceChangeEventProcessor eventProcessor;

    public Controller(PriceChangeEventProcessor eventProcessor) {
        this.eventProcessor = eventProcessor;
    }

    @GetMapping(path = "/prices", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<CoinUpdateDTO>> getPrices() {
        return Flux.create(sink ->
                eventProcessor.register(coinUpdate ->
                        sink.next(ServerSentEvent.<CoinUpdateDTO>builder()
                                .event("prices")
                                .data(coinUpdate)
                                .build()))
        );
    }

    @GetMapping(path = "/test", produces = MediaType.APPLICATION_JSON_VALUE)
    public String test() {
        return "Hi";
    }
}
