package com.onedigit.utah.api.impl.rest;

import com.onedigit.utah.api.impl.BaseExchangeAdapter;
import com.onedigit.utah.model.Exchange;
import com.onedigit.utah.model.NetworkAvailabilityDTO;
import com.onedigit.utah.model.api.common.RestResponse;
import com.onedigit.utah.model.api.mexc.rest.MexcRestResponse;
import com.onedigit.utah.model.api.mexc.rest.MexcRestResponseObject;
import com.onedigit.utah.service.MarketLocalCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static com.onedigit.utah.constants.ApiConstants.*;

@Slf4j
@Service
public class MexcAdapterImpl extends BaseExchangeAdapter {

    public MexcAdapterImpl(WebClient mexcRestApiClient) {
        this.webClient = mexcRestApiClient;
    }

    @Override
    public Flux<MexcRestResponse> watchMarketData() {
        log.info("Initiate getMarketData call from mexc");
        return getWithRepeat(MEXC_API_REST_GET_TICKERS, MexcRestResponseObject[].class, exchangeApiRetrySpec(log))
                .map(response -> new MexcRestResponse(Arrays.asList(response)));
    }

    public void storeMarketData(RestResponse response) {
        ((MexcRestResponse) response).getTickers().stream()
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

    @Override
    public Mono<List<NetworkAvailabilityDTO>> isWithdrawAvailable(String ticker) {
        return null;
    }

    @Override
    public Mono<List<NetworkAvailabilityDTO>> isDepositAvailable(String ticker) {
        return null;
    }
}