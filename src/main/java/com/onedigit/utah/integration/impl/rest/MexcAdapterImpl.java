package com.onedigit.utah.integration.impl.rest;

import com.onedigit.utah.integration.impl.BaseExchangeAdapter;
import com.onedigit.utah.integration.helpers.MexcApiHelper;
import com.onedigit.utah.model2.NetworkAvailabilityDTO;
import com.onedigit.utah.model2.integration.common.RestResponse;
import com.onedigit.utah.model2.integration.mexc.rest.MexcRestResponse;
import com.onedigit.utah.model2.integration.mexc.rest.MexcRestResponseCoinObject;
import com.onedigit.utah.model2.integration.mexc.rest.MexcRestResponseTickerObject;
import com.onedigit.utah.model2.Exchange;
import com.onedigit.utah.service.MarketLocalCache2;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import static com.onedigit.utah.constants.ApiConstants.*;

@Slf4j
@Service
public class MexcAdapterImpl extends BaseExchangeAdapter {

    @Value("${mexc.adapter.enabled}")
    @Getter
    protected boolean isEnabled;

    private final MarketLocalCache2 cache;

    private final MexcApiHelper apiHelper;

    public MexcAdapterImpl(@Qualifier("mexcRestApiClient") WebClient mexcRestApiClient, MarketLocalCache2 cache, MexcApiHelper apiHelper) {
        this.cache = cache;
        this.apiHelper = apiHelper;
        this.webClient = mexcRestApiClient;
    }

    @Override
    public Flux<MexcRestResponse> watchPrices() {
        log.info("Initiate watchPrices call from mexc");
        return getWithDelayedRepeat(MEXC_API_REST_GET_TICKERS, MexcRestResponseTickerObject[].class, Duration.ofMillis(REST_API_CALLS_FREQUENCY_MS), exchangeApiRetrySpec(log))
                .map(response -> new MexcRestResponse(Arrays.asList(response), null));
    }

    @Override
    public void populateSpreads(RestResponse response) {
        log.debug("response from mexc");
        ((MexcRestResponse) response).getTickers().stream()
                .filter(resp -> StringUtils.endsWith(resp.getSymbol(), "USDT"))
                .forEach(resp -> {
                    String tt = StringUtils.substringBefore(resp.getSymbol(), "USDT");
                    BigDecimal price = new BigDecimal(resp.getPrice());
                    val coin = cache.savePrice(tt, Exchange.MEXC, price);
                    val spreads = cache.calculateSpreads(coin);
                    if (spreads != null) {
                        //TODO: populate event to websocket client
                    }
                });
    }

    @Override
    public Flux<? extends RestResponse> watchAvailability() {
        log.info("Initiate watchAvailability call from mexc");
        return getWithDelayedRepeat(MEXC_API_REST_GET_CURRENCY_INFO,
                params -> params.putAll(apiHelper.buildParamsWithSignature()),
                httpHeaders -> httpHeaders.addAll(apiHelper.buildHeaders()),
                MexcRestResponseCoinObject[].class,
                Duration.ofMillis(REST_API_GET_AVAILABILITY_FREQUENCY_MS),
                exchangeApiRetrySpec(log))
                .map(mexcRestResponseCoinObjects -> new MexcRestResponse(null, Arrays.asList(mexcRestResponseCoinObjects)));

    }

    @Override
    public void populateAvailability(RestResponse response) {
        log.debug("response availability time: {}", ZonedDateTime.now());
        ((MexcRestResponse) response).getCoins().stream()
                .filter(currency -> cache.hasPricesFor(currency.getCoin()))
                .forEach(currency -> {
                    List<NetworkAvailabilityDTO> naDTOs = currency.getChains().stream().map(chain ->
                            NetworkAvailabilityDTO.builder()
                                    .networkChainName(chain.getNetwork())
                                    .isWithdrawAvailable(chain.isWithdrawalEnable())
                                    .isDepositAvailable(chain.isDepositEnable())
                                    .minWithdrawalFee(chain.getWithdrawalFee())
                                    .build()
                    ).toList();
                    if (naDTOs != null) {
                        //TODO: populate event to websocket client
                    }
                });
    }

}