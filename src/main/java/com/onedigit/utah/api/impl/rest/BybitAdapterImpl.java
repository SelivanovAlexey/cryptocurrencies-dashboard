package com.onedigit.utah.api.impl.rest;

import com.onedigit.utah.api.BybitApiHelper;
import com.onedigit.utah.api.impl.BaseExchangeAdapter;
import com.onedigit.utah.model.Exchange;
import com.onedigit.utah.model.NetworkAvailabilityDTO;
import com.onedigit.utah.model.TransferType;
import com.onedigit.utah.model.api.bybit.rest.BybitRestChain;
import com.onedigit.utah.model.api.bybit.rest.BybitRestResponse;
import com.onedigit.utah.model.api.common.RestResponse;
import com.onedigit.utah.service.MarketLocalCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Override
    public Mono<List<NetworkAvailabilityDTO>> isWithdrawAvailable(String ticker) {
        log.info("Initiate isWithdrawAvailable call from bybit");
        Map<String, List<String>> paramsMap = Map.of("coin", List.of(ticker));

        return get(BYBIT_API_REST_GET_COIN_INFO,
                paramsMap,
                httpHeaders -> httpHeaders.putAll(BybitApiHelper.buildHeadersWithSignature(paramsMap)),
                BybitRestResponse.class)
                .map(response -> transformAvailabilityResponse(response, TransferType.WITHDRAW));
    }


    @Override
    public Mono<List<NetworkAvailabilityDTO>> isDepositAvailable(String ticker) {
        log.info("Initiate isDepositAvailable call from bybit");
        Map<String, List<String>> paramsMap = Map.of("coin", List.of(ticker));

        return get(BYBIT_API_REST_GET_COIN_INFO,
                paramsMap,
                httpHeaders -> httpHeaders.putAll(BybitApiHelper.buildHeadersWithSignature(paramsMap)),
                BybitRestResponse.class)
                .map(response -> transformAvailabilityResponse(response, TransferType.DEPOSIT));
    }

    //TODO: NPE checks and error handling
    private List<NetworkAvailabilityDTO> transformAvailabilityResponse(BybitRestResponse response, TransferType type) {
        return response.getResult().getRows().get(0).getChains().stream()
                .map(chain ->
                        NetworkAvailabilityDTO.builder()
                                .networkChainName(chain.getChain())
                                .networkChainType(chain.getChainType())
                                .type(type)
                                .isAvailable("1".equals(getAvailabilityFromTransferType(chain, type)))
                                .build())
                .collect(Collectors.toList());
    }

    private String getAvailabilityFromTransferType(BybitRestChain chain, TransferType type) {
        String result = "0";
        switch (type) {
            case DEPOSIT -> result = chain.getChainDeposit();
            case WITHDRAW -> result = chain.getChainWithdraw();
        }
        return result;
    }
}
