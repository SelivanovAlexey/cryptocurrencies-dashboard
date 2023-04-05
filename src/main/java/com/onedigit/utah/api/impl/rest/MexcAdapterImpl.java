package com.onedigit.utah.api.impl.rest;

import com.onedigit.utah.api.ExchangeAdapter;
import com.onedigit.utah.model.Exchange;
import com.onedigit.utah.model.api.mexc.rest.MexcRestResponse;
import com.onedigit.utah.service.MarketLocalCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;

import static com.onedigit.utah.constants.ApiConstants.*;


/**
 * Implemented with REST protocol
 */
@Slf4j
@Service
public class MexcAdapterImpl extends ExchangeAdapter {

    private Boolean isConnectionActive = false;

    private final WebClient mexcRestApiClient;

    public MexcAdapterImpl(WebClient mexcRestApiClient) {
        this.mexcRestApiClient = mexcRestApiClient;
    }

    @Override
    public Mono<Void> getMarketData() {
        log.info("Started getMarketData from mexc");
        return mexcRestApiClient
                .get()
                .uri(uruBuilder ->
                        uruBuilder
                                .path(MEXC_API_REST_GET_TICKERS)
                                .build())
                .retrieve()
                .bodyToMono(MexcRestResponse[].class)
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
                        }
                ).then();
    }

    private MexcRestResponse[] storeTickersData(MexcRestResponse[] response) {
        Arrays.stream(response)
                .filter(resp -> StringUtils.endsWith(resp.getSymbol(), "USDT"))
                .forEach(resp -> {
                    String tt = StringUtils.substringBefore(resp.getSymbol(), "USDT");
                    BigDecimal price = new BigDecimal(resp.getPrice());

                    MarketLocalCache.put(tt, Exchange.MEXC, price);
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
        return Exchange.MEXC;
    }
}