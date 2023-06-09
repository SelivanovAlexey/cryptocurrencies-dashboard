package com.onedigit.utah.api.impl.rest;

import com.onedigit.utah.api.impl.BaseExchangeAdapter;
import com.onedigit.utah.model.Exchange;
import com.onedigit.utah.model.api.common.RestResponse;
import com.onedigit.utah.model.api.kucoin.rest.KucoinRestResponse;
import com.onedigit.utah.service.MarketLocalCache;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;

import static com.onedigit.utah.constants.ApiConstants.*;

@Slf4j
@Service
public class KucoinAdapterImpl extends BaseExchangeAdapter {

    public KucoinAdapterImpl(@Qualifier("kucoinRestApiClient") WebClient kucoinRestApiClient) {
        this.webClient = kucoinRestApiClient;
    }

    @Override
    @SneakyThrows
    public Flux<KucoinRestResponse> watchPrices() {
        log.info("Initiate getMarkedData call from kucoin");
        return getWithRepeat(KUCOIN_API_REST_GET_TICKERS, KucoinRestResponse.class, exchangeApiRetrySpec(log));
    }

    @Override
    public void storePrices(RestResponse response) {
        ((KucoinRestResponse) response).getData().getTickerList().stream()
                .filter(ticker -> StringUtils.endsWith(ticker.getSymbol(), "-USDT"))
                .forEach(ticker -> {
                    String tt = StringUtils.substringBefore(ticker.getSymbol(), "-USDT");
                    BigDecimal price = new BigDecimal(ticker.getLast());

                    MarketLocalCache.savePrice(tt, getExchangeName(), price);
                });
    }

    public Boolean isEnabled() {
        return true;
    }

    @Override
    public Exchange getExchangeName() {
        return Exchange.KUCOIN;
    }

    @Override
    public Flux<? extends RestResponse> watchAvailability() {
        return null;
    }

    @Override
    public void storeAvailability(RestResponse response) {

    }
}


