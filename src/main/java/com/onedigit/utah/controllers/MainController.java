package com.onedigit.utah.controllers;

import com.onedigit.utah.api.ExchangeAdapter;
import com.onedigit.utah.model.CoinDTO;
import com.onedigit.utah.model.Exchange;
import com.onedigit.utah.service.MarketLocalCache;
import com.onedigit.utah.service.event.CacheChangedEventProcessor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RequestMapping("/api")
@RestController
//TODO: remove hardcode
@CrossOrigin(origins = "http://localhost:63342")
public class MainController {
    private final CacheChangedEventProcessor eventProcessor;

    private final Map<Exchange, ExchangeAdapter> adapters;

    public MainController(CacheChangedEventProcessor eventProcessor, Map<Exchange, ExchangeAdapter> adapters) {
        this.eventProcessor = eventProcessor;
        this.adapters = adapters;
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

    @GetMapping(path = "/getTickerInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CoinDTO> getTickerInfo(@RequestParam(value = "ticker") String ticker) {
        if (!MarketLocalCache.isTickerExists(ticker)) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(MarketLocalCache.getTickerInfo(ticker));
        }
    }

    @GetMapping(path = "/enablePrices", produces = MediaType.APPLICATION_JSON_VALUE)
    public void enablePricesFor(@RequestParam(value = "ticker") String ticker) {
        MarketLocalCache.enableVerboseInfo(ticker);
    }
}
