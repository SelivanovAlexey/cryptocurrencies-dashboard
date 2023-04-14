package com.onedigit.utah.api.impl.rest;

import com.onedigit.utah.api.impl.BaseExchangeAdapter;
import com.onedigit.utah.model.Connection;
import com.onedigit.utah.model.Exchange;
import com.onedigit.utah.model.api.mexc.rest.MexcRestResponse;
import com.onedigit.utah.service.MarketLocalCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.Arrays;

import static com.onedigit.utah.constants.ApiConstants.*;


/**
 * Implemented with REST protocol
 */
@Slf4j
@Service
public class MexcAdapterImpl extends BaseExchangeAdapter {

    public MexcAdapterImpl(WebClient mexcRestApiClient) {
        this.webClient = mexcRestApiClient;
    }

    @Override
    public void getMarketData() {
        log.info("Initiate getMarketData call from mexc");
        get(MEXC_API_REST_GET_TICKERS, MexcRestResponse[].class)
                .repeat()
                .retryWhen(exchangeApiRetrySpec(log))
                .subscribe(response -> {
                    storeTickersData(response);
                    setConnectionStatus(Connection.ACTIVE);
                });
    }

    private void storeTickersData(MexcRestResponse[] response) {
        Arrays.stream(response)
                .filter(resp -> StringUtils.endsWith(resp.getSymbol(), "USDT"))
                .forEach(resp -> {
                    String tt = StringUtils.substringBefore(resp.getSymbol(), "USDT");
                    BigDecimal price = new BigDecimal(resp.getPrice());

                    MarketLocalCache.put(tt, Exchange.MEXC, price);
                });
    }

    @Override
    public Boolean isEnabled() {
        return true;
    }

    @Override
    public Exchange getExchangeName() {
        return Exchange.MEXC;
    }
}