package com.onedigit.utah.api.impl;

import com.onedigit.utah.api.ExchangeAdapter;
import com.onedigit.utah.model.Exchange;
import com.onedigit.utah.model.api.bybit.rest.BybitRestResponse;
import com.onedigit.utah.service.MarketLocalCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;

import static com.onedigit.utah.constants.ApiConstants.*;

/**
 * Implemented with REST protocol
 */
@Service
@Slf4j
public class BybitAdapterImpl implements ExchangeAdapter {

    private final WebClient bybitRestApiClient;

    public BybitAdapterImpl(@Qualifier("bybitApiClient") WebClient bybitRestApiClient) {
        this.bybitRestApiClient = bybitRestApiClient;
    }

    @Override
    public Mono<Void> getMarketData() {
        log.debug("Started getMarketData from bybit");
        return getAllTickers();
    }

    /**
     * Retrieves only -USDT tickers
     */
    private Mono<Void> getAllTickers(){
        return bybitRestApiClient
                .get()
                .uri(uruBuilder ->
                    uruBuilder
                            .path(BYBIT_API_REST_GET_TICKERS)
                            .queryParam("category", "spot")
                            .build())
                .retrieve()
                .bodyToMono(BybitRestResponse.class)
                .delaySubscription(Duration.ofMillis(BYBIT_REST_CALLS_FREQUENCY_MS))
                .repeat()
                .map(this::storeTickersData).then();
    }

    private BybitRestResponse storeTickersData(BybitRestResponse response){
        response.getResult().getTickers().stream()
                .filter(ticker -> StringUtils.endsWith(ticker.getSymbol(), "USDT"))
                .forEach(ticker -> {
                    String tt = StringUtils.substringBefore(ticker.getSymbol(), "USDT");
                    BigDecimal price = new BigDecimal(ticker.getLastPrice());
                    MarketLocalCache.put(tt, Exchange.BYBIT, price);
                });
        return response;
    }
}
