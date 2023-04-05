package com.onedigit.utah.api.impl.rest;

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
@Slf4j
@Service
public class BybitAdapterImpl extends ExchangeAdapter {

    private final WebClient bybitRestApiClient;

    private Boolean isConnectionActive = false;

    public BybitAdapterImpl(@Qualifier("bybitRestApiClient") WebClient bybitRestApiClient) {
        this.bybitRestApiClient = bybitRestApiClient;
    }

    /**
     * Retrieves only -USDT tickers
     */
    @Override
    public Mono<Void> getMarketData() {
        log.info("Started getMarketData from bybit");
        return bybitRestApiClient
                .get()
                .uri(uruBuilder ->
                        uruBuilder
                                .path(BYBIT_API_REST_GET_TICKERS)
                                .queryParam("category", "spot")
                                .build())
                .retrieve()
                .bodyToMono(BybitRestResponse.class)
                .map(response -> {
                    if (!isConnectionActive) isConnectionActive = true;
                    return response;
                })
                .delaySubscription(Duration.ofMillis(REST_API_CALLS_FREQUENCY_MS))
                .repeat()
                .map(this::storeTickersData)
                .doOnError(error -> {
                    log.error("Error during communication with server:", error);
                    isConnectionActive = false;
                })
                .then();
    }

    private BybitRestResponse storeTickersData(BybitRestResponse response) {
        response.getResult().getTickers().stream()
                .filter(ticker -> StringUtils.endsWith(ticker.getSymbol(), "USDT"))
                .forEach(ticker -> {
                    String tt = StringUtils.substringBefore(ticker.getSymbol(), "USDT");
                    BigDecimal price = new BigDecimal(ticker.getLastPrice());

                    MarketLocalCache.put(tt, getExchangeName(), price);

                });
        return response;
    }

    @Override
    public Boolean isEnabled() {
        return true;
    }

    @Override
    public Boolean isConnectionActive() {
        return isConnectionActive;
    }

    @Override
    public Exchange getExchangeName() {
        return Exchange.BYBIT;
    }
}
