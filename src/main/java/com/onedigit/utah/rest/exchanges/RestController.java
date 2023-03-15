package com.onedigit.utah.rest.exchanges;

import com.onedigit.utah.model.CoinDTO;
import com.onedigit.utah.service.MarketLocalCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

import static com.onedigit.utah.constants.ApiConstants.FRONTEND_UPDATE_FREQUENCY;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/api/kucoin")
@Slf4j
public class RestController {

    @GetMapping(path = "/prices", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<List<CoinDTO>>> getPrices() {
        Mono<ServerSentEvent<List<CoinDTO>>> firstFrame = Mono.just(MarketLocalCache.getAllExchangesData())
                .map(coins -> ServerSentEvent.<List<CoinDTO>>builder()
                        .id(String.valueOf(coins.size()))
                        .event("prices")
                        .data(coins)
                        .comment("Full market data")
                        .build());

        Flux<ServerSentEvent<List<CoinDTO>>> stream = Flux.interval(Duration.ofMillis(FRONTEND_UPDATE_FREQUENCY))
                .map(sequence -> MarketLocalCache
                        .getValuesToUpdate())
                .map(coins -> ServerSentEvent.<List<CoinDTO>>builder()
                        .id(String.valueOf(coins.size()))
                        .event("prices")
                        .data(coins)
                        .build());
        return Flux.concat(firstFrame, stream);
    }

    @GetMapping(path = "/test", produces = MediaType.APPLICATION_JSON_VALUE)
    public String test() {
        return "Hi";
    }
}
