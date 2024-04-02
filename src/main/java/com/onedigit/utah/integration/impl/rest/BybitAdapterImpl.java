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
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.time.Duration;
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
    public Flux<BybitRestResponse> watchPrices() {
        log.info("Initiate watchPrices call from bybit");
        return getWithDelayedRepeat(BYBIT_API_REST_GET_TICKERS,
                Map.of("category", List.of("spot")),
                BybitRestResponse.class,
                Duration.ofMillis(REST_API_CALLS_FREQUENCY_MS),
                exchangeApiRetrySpec(log));
    }

    @Override
    public void populateSpreads(RestResponse response) {
        log.debug("response from bybit");
        ((BybitRestResponse) response).getResult().getTickers().stream()
                .filter(ticker -> StringUtils.endsWith(ticker.getSymbol(), "USDT"))
                .forEach(ticker -> {
                    String tt = StringUtils.substringBefore(ticker.getSymbol(), "USDT");
                    BigDecimal price = new BigDecimal(ticker.getLastPrice());
                    val coin = cache.savePrice(tt, Exchange.BYBIT, price);
                    val spreads = cache.calculateSpreads(coin);
                    if (spreads != null) {
                        //TODO: populate event to websocket client
                    }
                });
    }

    @Override
    public Flux<? extends RestResponse> watchAvailability() {
        log.info("Initiate watchAvailability call from bybit");
        MultiValueMap<String, String> params = CollectionUtils.toMultiValueMap(Map.of("category", List.of("spot")));
        return getWithDelayedRepeat(BYBIT_API_REST_GET_COIN_INFO,
                queryParams -> queryParams.putAll(params),
                httpHeaders -> httpHeaders.addAll(apiHelper.buildHeadersWithSignature(params)),
                BybitRestResponse.class,
                Duration.ofMillis(REST_API_GET_AVAILABILITY_FREQUENCY_MS),
                exchangeApiRetrySpec(log));
    }

    public void populateAvailability(RestResponse response) {
        ((BybitRestResponse) response).getResult().getRows().stream()
                .filter(currency -> cache.hasPricesFor(currency.getCoin()))
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
    }
}
