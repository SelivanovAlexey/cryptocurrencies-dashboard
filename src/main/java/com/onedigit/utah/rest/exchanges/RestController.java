package com.onedigit.utah.rest.exchanges;

import com.onedigit.utah.model.api.kucoin.JsonKucoinResponseDTO;
import com.onedigit.utah.model.api.kucoin.KucoinDataDTO;
import com.onedigit.utah.model.api.kucoin.KucoinTickerDTO;
import com.onedigit.utah.ws.KucoinAdapterImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;
import java.util.stream.Collectors;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/api/kucoin")
@Slf4j
public class RestController{
    private final WebClient kucoinWebClient;
    private final KucoinAdapterImpl adapter;

    public RestController(@Qualifier("kucoinApiClient") WebClient kucoinWebClient, KucoinAdapterImpl adapter) {
        this.kucoinWebClient = kucoinWebClient;
        this.adapter = adapter;
    }

    @GetMapping(value = "/prices")
    public Flux<ServerSentEvent<Map<String, String>>> getPrices() {
        return Flux.interval(Duration.ofSeconds(1))
                .flatMap(sequence -> getListFromExchange())
                .map(coinMap -> ServerSentEvent.<Map<String, String>>builder()
                        .event("price.kucoin")
                        .data(coinMap)
                        .build());
    }

    @GetMapping(value = "/prices-block")
    public Map<String, String> getPricesBlock() {
        return getListFromExchange().blockFirst();
    }

    private Flux<Map<String, String>> getListFromExchange() {
        Flux<JsonKucoinResponseDTO> response = kucoinWebClient
                .get()
                .uri("/api/v1/market/allTickers")
                .retrieve().bodyToFlux(JsonKucoinResponseDTO.class);

        return response
                .map(JsonKucoinResponseDTO::getData)
                .map(KucoinDataDTO::getTickerList)
                .map(tickerList ->
                        tickerList
                                .stream()
                                .filter(kucoinTickerDTO -> kucoinTickerDTO.getSymbol().endsWith("USDT"))
                                .filter(kucoinTickerDTO -> kucoinTickerDTO.getLast() != null)
                                .collect(Collectors.toMap(tickerDto -> StringUtils.substringBefore(tickerDto.getSymbol(), "-USDT"), KucoinTickerDTO::getLast)));
    }

    //TODO: rest to be triggered on page load (or on an app start)
    @GetMapping(path = "/test", produces = MediaType.APPLICATION_JSON_VALUE)
    public void testFlux() throws Exception {
        adapter.getMarketData().subscribe();
    }
}
