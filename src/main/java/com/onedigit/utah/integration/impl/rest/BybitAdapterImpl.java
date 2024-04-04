package com.onedigit.utah.integration.impl.rest;

import com.onedigit.utah.integration.helpers.BybitApiHelper;
import com.onedigit.utah.integration.impl.BaseExchangeAdapter;
import com.onedigit.utah.model2.NetworkAvailabilityDTO;
import com.onedigit.utah.model2.integration.bybit.rest.BybitRestResponse;
import com.onedigit.utah.model2.integration.common.RestResponse;
import com.onedigit.utah.model2.Exchange;
import com.onedigit.utah.service.MarketLocalCache2;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import static com.onedigit.utah.constants.ApiConstants.*;

@Slf4j
@Service
public class BybitAdapterImpl extends BaseExchangeAdapter {
    @Value("${bybit.adapter.enabled}")
    @Getter
    protected boolean isEnabled;

    private final MarketLocalCache2 cache;

    private final BybitApiHelper apiHelper;

    public BybitAdapterImpl(@Qualifier("bybitRestApiClient") WebClient bybitRestApiClient, MarketLocalCache2 cache, BybitApiHelper apiHelper) {
        this.cache = cache;
        this.apiHelper = apiHelper;
        this.webClient = bybitRestApiClient;
    }

    /**
     * Retrieves only -USDT tickers
     */
    @Override
    public Mono<BybitRestResponse> getPrices() {
        log.info("Initiate getPrices call from bybit");
        return get(BYBIT_API_REST_GET_TICKERS,
                Map.of("category", List.of("spot")),
                BybitRestResponse.class);
    }

    @Override
    public RestResponse populateSpreads(RestResponse response) {
        log.debug("response prices from bybit");
        ((BybitRestResponse) response).getResult().getTickers().stream()
                .filter(ticker -> StringUtils.endsWith(ticker.getSymbol(), "USDT"))
                .forEach(ticker -> {
                    String tt = StringUtils.substringBefore(ticker.getSymbol(), "USDT");
                    BigDecimal price = new BigDecimal(ticker.getLastPrice());
                    val coin = cache.savePrice(tt, Exchange.BYBIT, price);
                    if(coin != null){
                        val spreads = cache.calculateSpreads(coin);
                    }
//                    if (spreads != null) {
//                        //TODO: populate event to websocket client
//                    }
                });
        return response;
    }

    @Override
    public Mono<? extends RestResponse> getAvailability() {
        log.info("Initiate getAvailability call from bybit");
        MultiValueMap<String, String> params = CollectionUtils.toMultiValueMap(Map.of("category", List.of("spot")));
        return get(BYBIT_API_REST_GET_COIN_INFO,
                queryParams -> queryParams.putAll(params),
                httpHeaders -> httpHeaders.addAll(apiHelper.buildHeadersWithSignature(params)),
                BybitRestResponse.class);
    }

    public RestResponse populateAvailability(RestResponse response) {
        log.debug("response availability from bybit");
        ((BybitRestResponse) response).getResult().getRows().stream()
                .filter(currency -> cache.hasPricesFor(Exchange.BYBIT, currency.getCoin()))
                .forEach(currency -> {
                    List<NetworkAvailabilityDTO> naDTOs = currency.getChains().stream().map(chain ->
                            NetworkAvailabilityDTO.builder()
                                    .networkChainName(chain.getChain())
                                    .networkChainType(chain.getChainType())
                                    .isWithdrawAvailable(chain.getChainWithdraw().equals("1"))
                                    .isDepositAvailable(chain.getChainWithdraw().equals("1"))
                                    .minWithdrawalFee(new BigDecimal(chain.getWithdrawFee()))
                                    .build()
                    ).toList();
                    if (naDTOs != null) {
                        //TODO: populate event to websocket client
                    }
                });
        return response;
    }
}
