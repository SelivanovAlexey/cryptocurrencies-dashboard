package com.onedigit.utah.controllers;

import com.onedigit.utah.model.CoinDTO;
import com.onedigit.utah.service.MarketLocalCache;
import com.onedigit.utah.service.event.PriceChangeEventProcessor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequestMapping("/api")
@RestController
public class MainController {
    private final PriceChangeEventProcessor eventProcessor;

    public MainController(PriceChangeEventProcessor eventProcessor) {
        this.eventProcessor = eventProcessor;
    }

    @GetMapping(path = "/prices", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<CoinDTO>> getPrices() {
        //TODO: sometimes there is spreads in response but without price
        Flux<ServerSentEvent<CoinDTO>> firstFrame = Mono.just(MarketLocalCache.getAllExchangesData())
                .flatMapMany(Flux::fromIterable)
                .map(coins -> ServerSentEvent.<CoinDTO>builder()
                        .event("prices")
                        .data(coins)
                        .comment("Full market data")
                        .build());

        Flux<ServerSentEvent<CoinDTO>> mainFlow = Flux.create(sink ->
                eventProcessor.register(coinUpdate ->
                        sink.next(ServerSentEvent.<CoinDTO>builder()
                                .event("prices")
                                .data(coinUpdate)
                                .build())));

        return Flux.concat(firstFrame, mainFlow);
    }

    @GetMapping(path = "/test", produces = MediaType.APPLICATION_JSON_VALUE)
    public String test() {
        return "Hi";
    }


    @GetMapping(path = "/getTickerInfo/{ticker}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CoinDTO> getTickerInfo(@PathVariable String ticker) {
        if (!MarketLocalCache.isTickerExists(ticker)) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(MarketLocalCache.getTickerInfo(ticker));
        }
    }

    @GetMapping(path = "/enablePrices/{ticker}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void enablePricesFor(@PathVariable String ticker) {
        MarketLocalCache.enablePriceForTicker(ticker);
    }

    @GetMapping(path = "/disablePrices", produces = MediaType.APPLICATION_JSON_VALUE)
    public void disablePrices(@PathVariable String ticker) {
        MarketLocalCache.disablePriceForTicker(ticker);
    }
}
