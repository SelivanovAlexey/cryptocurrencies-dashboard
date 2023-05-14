package com.onedigit.utah.api.impl.rest;

import com.onedigit.utah.api.BybitApiHelper;
import com.onedigit.utah.api.impl.BaseExchangeAdapter;
import com.onedigit.utah.model.Exchange;
import com.onedigit.utah.model.NetworkAvailabilityDTO;
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
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.onedigit.utah.constants.ApiConstants.*;

@Slf4j
@Service
public class BybitAdapterImpl extends BaseExchangeAdapter {

    private final BybitApiHelper apiHelper;

    public BybitAdapterImpl(@Qualifier("bybitRestApiClient") WebClient bybitRestApiClient, BybitApiHelper apiHelper) {
        this.apiHelper = apiHelper;
        this.webClient = bybitRestApiClient;
    }

    /**
     * Retrieves only -USDT tickers
     */
    @Override
    public Flux<BybitRestResponse> watchPrices() {
        log.info("Initiate getMarketData call from bybit");
        return getWithRepeat(BYBIT_API_REST_GET_TICKERS,
                Map.of("category", List.of("spot")),
                BybitRestResponse.class,
                exchangeApiRetrySpec(log));
    }

    @Override
    public void storePrices(RestResponse response) {
        ((BybitRestResponse) response).getResult().getTickers().stream()
                .filter(ticker -> StringUtils.endsWith(ticker.getSymbol(), "USDT"))
                .forEach(ticker -> {
                    String tt = StringUtils.substringBefore(ticker.getSymbol(), "USDT");
                    BigDecimal price = new BigDecimal(ticker.getLastPrice());

                    MarketLocalCache.savePrice(tt, getExchangeName(), price);
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

    @Override
    public Flux<? extends RestResponse> watchAvailability() {
        log.info("Initiate getAvailability call from bybit");
        Map<String, List<String>> paramsMap = Collections.emptyMap();

        return getWithDelayedRepeat(BYBIT_API_REST_GET_COIN_INFO,
                paramsMap,
                httpHeaders -> httpHeaders.putAll(apiHelper.buildHeadersWithSignature(paramsMap)),
                BybitRestResponse.class,
                Duration.ofMillis(REST_API_GET_AVAILABILITY_FREQUENCY_MS),
                exchangeApiRetrySpec(log));
    }

    //TODO: NPE checks and error handling
    @Override
    public void storeAvailability(RestResponse response) {
        ((BybitRestResponse) response).getResult().getRows()
                .forEach(coinRow -> {
                    List<NetworkAvailabilityDTO> availabilityList = coinRow.getChains().stream()
                            .map(chain ->
                                    NetworkAvailabilityDTO.builder()
                                            .networkChainName(chain.getChain())
                                            .networkChainType(chain.getChainType())
                                            .isDepositAvailable("1".equals(chain.getChainDeposit()))
                                            .isWithdrawAvailable("1".equals(chain.getChainWithdraw()))
                                            .build()).toList();
                    MarketLocalCache.saveAvailability(coinRow.getCoin(), getExchangeName(), availabilityList);
                });
    }
}
