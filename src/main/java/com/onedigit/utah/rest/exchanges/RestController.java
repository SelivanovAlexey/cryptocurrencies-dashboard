package com.onedigit.utah.rest.exchanges;

import com.onedigit.utah.model.Exchange;
import com.onedigit.utah.service.MarketLocalCache;
import com.onedigit.utah.ws.KucoinAdapterImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Map;

import static com.onedigit.utah.constants.ApiConstants.FRONTEND_UPDATE_FREQUENCY;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/api/kucoin")
@Slf4j
public class RestController {
    private final KucoinAdapterImpl adapter;

    public RestController(KucoinAdapterImpl adapter) {
        this.adapter = adapter;
    }

    //TODO: rest to be triggered on page load (or on an app start)
    @GetMapping(path = "/start", produces = MediaType.APPLICATION_JSON_VALUE)
    public void initiateDataRetrieving() throws Exception {
        MarketLocalCache.flush();
        adapter.getMarketData().subscribe();
    }

    //TODO: to change first frame behaviour
    //TODO: to add spread calculation
    @GetMapping(path = "/prices", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Map<String, Double>>> getPrices() {
        MarketLocalCache.flush();
        return Flux.interval(Duration.ofMillis(FRONTEND_UPDATE_FREQUENCY))
                .map(sequence ->  MarketLocalCache
                        .getAllExchangesData()
                        .get(Exchange.KUCOIN)
                        .getValuesToUpdate())
                .map(coins -> ServerSentEvent.<Map<String,Double>>builder()
                        .id("Size: " + coins.size())
                        .event("prices.kucoin")
                        .data(coins)
                        .build());
    }

    @GetMapping(path = "/test", produces = MediaType.APPLICATION_JSON_VALUE)
    public String test(){
        return "Hi";
    }
}
