package com.onedigit.utah.integration.impl.rest;

import com.onedigit.utah.integration.impl.BaseExchangeAdapter;
import com.onedigit.utah.model2.NetworkAvailabilityDTO;
import com.onedigit.utah.model2.SpreadDTO;
import com.onedigit.utah.model2.integration.common.RestResponse;
import com.onedigit.utah.model2.integration.kucoin.rest.KucoinRestResponse;
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
import java.util.List;

import static com.onedigit.utah.constants.ApiConstants.*;

@Slf4j
@Service
public class KucoinAdapterImpl extends BaseExchangeAdapter {

    @Value("${kucoin.adapter.enabled}")
    @Getter
    protected boolean isEnabled;

    private final MarketLocalCache2 cache;

    public KucoinAdapterImpl(@Qualifier("kucoinRestApiClient") WebClient kucoinRestApiClient, MarketLocalCache2 cache) {
        this.cache = cache;
        this.webClient = kucoinRestApiClient;
    }

    @Override
    public Flux<KucoinRestResponse> watchPrices() {
        log.info("Initiate watchPrices call from kucoin");
        return getWithDelayedRepeat(KUCOIN_API_REST_GET_TICKERS, KucoinRestResponse.class, Duration.ofMillis(REST_API_CALLS_FREQUENCY_MS), exchangeApiRetrySpec(log));
    }

    @Override
    public void populateSpreads(RestResponse response) {
        log.debug("response from kucoin");
        ((KucoinRestResponse) response).getData().get(0).getTickerList().stream()
                .filter(ticker -> StringUtils.endsWith(ticker.getSymbol(), "-USDT"))
                .forEach(ticker -> {
                    String tt = StringUtils.substringBefore(ticker.getSymbol(), "-USDT");
                    BigDecimal price = new BigDecimal(ticker.getLast());
                    val coin = cache.savePrice(tt, Exchange.KUCOIN, price);
                    val spreads = cache.calculateSpreads(coin);
                    if (spreads != null) {
                        //TODO: populate event to websocket client
                    }
                });
    }

    @Override
    public Flux<? extends RestResponse> watchAvailability() {
        log.info("Initiate watchAvailability call from kucoin");
        return getWithDelayedRepeat(KUCOIN_API_REST_GET_CURRENCY_LIST, KucoinRestResponse.class, Duration.ofMillis(REST_API_GET_AVAILABILITY_FREQUENCY_MS), exchangeApiRetrySpec(log));
    }

    @Override
    public void populateAvailability(RestResponse response) {
        ((KucoinRestResponse) response).getData().get(0).getCurrencyList().stream()
                .filter(currency -> cache.hasPricesFor(currency.getCurrency()))
                .forEach(currency -> {
                    List<NetworkAvailabilityDTO> naDTOs = currency.getChains().stream().map(chain ->
                            NetworkAvailabilityDTO.builder()
                                    .networkChainName(chain.getChainId())
                                    .networkChainType(chain.getChainName())
                                    .isWithdrawAvailable(chain.isWithdrawEnabled())
                                    .isDepositAvailable(chain.isDepositEnabled())
                                    .minWithdrawalFee(chain.getWithdrawalMinFee())
                                    .build()
                    ).toList();
                    if (naDTOs != null) {
                        //TODO: populate event to websocket client
                    }
                });
    }
}


