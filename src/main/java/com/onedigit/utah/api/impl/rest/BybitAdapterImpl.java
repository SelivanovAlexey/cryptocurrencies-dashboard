package com.onedigit.utah.api.impl.rest;

import com.onedigit.utah.api.impl.BaseExchangeAdapter;
import com.onedigit.utah.model.Exchange;
import com.onedigit.utah.model.api.bybit.rest.BybitRestResponse;
import com.onedigit.utah.model.api.common.RestResponse;
import com.onedigit.utah.service.MarketLocalCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static com.onedigit.utah.constants.ApiConstants.*;

@Slf4j
@Service
public class BybitAdapterImpl extends BaseExchangeAdapter {

    public BybitAdapterImpl(@Qualifier("bybitRestApiClient") WebClient bybitRestApiClient) {
        this.webClient = bybitRestApiClient;
    }

    /**
     * Retrieves only -USDT tickers
     */
    @Override
    public Flux<BybitRestResponse> watchMarketData() {
        log.info("Initiate getMarketData call from bybit");
        return getWithRepeat(BYBIT_API_REST_GET_TICKERS,
                Map.of("category", List.of("spot")),
                BybitRestResponse.class,
                exchangeApiRetrySpec(log));
    }

    @Override
    public void storeMarketData(RestResponse response) {
        ((BybitRestResponse) response).getResult().getTickers().stream()
                .filter(ticker -> StringUtils.endsWith(ticker.getSymbol(), "USDT"))
                .forEach(ticker -> {
                    String tt = StringUtils.substringBefore(ticker.getSymbol(), "USDT");
                    BigDecimal price = new BigDecimal(ticker.getLastPrice());

                    MarketLocalCache.put(tt, getExchangeName(), price);
                });
    }

    @Override
    public Boolean isEnabled() {
        return true;
    }

    @Override
    public Exchange getExchangeName() {
        return Exchange.BYBIT;
    }
}
