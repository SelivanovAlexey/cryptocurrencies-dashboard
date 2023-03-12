package com.onedigit.utah.rest.exchanges;

import com.onedigit.utah.model.CoinDTO;
import com.onedigit.utah.model.api.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bybit")
@Slf4j
public class BybitController {
    private final WebClient bybitWebClient;

    public BybitController(@Qualifier("bybitApiClient") WebClient bybitWebClient) {
        this.bybitWebClient = bybitWebClient;
    }

    @GetMapping(value = "/prices")
    public Flux<ServerSentEvent<Map<String,String>>> getPrices(){
        List<CoinDTO> coinList = new ArrayList<>();
        return Flux.interval(Duration.ofSeconds(1))
                .flatMap(sequence -> getListFromExchange())
                .map(coinMap -> ServerSentEvent.<Map<String,String>>builder()
                        .event("price.bybit")
                        .data(coinMap)
                        .build());
    }

    @GetMapping(value = "/prices-block")
    public Map<String, String> getPricesBlock(){
        return getListFromExchange().blockFirst();
    }

    private Flux<Map<String, String>> getListFromExchange(){
        Flux<JsonBybitResponseDTO> response = bybitWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v5/market/tickers")
                        .queryParam("category", "spot")
                        .build())
                .retrieve().bodyToFlux(JsonBybitResponseDTO.class);

        return response
                .map(JsonBybitResponseDTO::getResult)
                .map(BybitResultDTO::getTickers)
                .map(tickerList ->
                        tickerList
                                .stream()
                                .filter(bybitTickerDTO -> bybitTickerDTO.getSymbol().endsWith("USDT"))
                                .collect(Collectors.toMap(tickerDto -> StringUtils.substringBefore(tickerDto.getSymbol(), "USDT"), BybitTickerDTO::getLastPrice)));
    };
}
