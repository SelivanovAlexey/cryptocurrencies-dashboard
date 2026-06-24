package com.onedigit.utah.integration.impl.rest;

import com.onedigit.utah.integration.impl.BaseExchangeAdapter;
import com.onedigit.utah.model2.api.NetworkAvailabilityDTO;
import com.onedigit.utah.model2.api.SpreadDTO;
import com.onedigit.utah.model2.integration.common.RestResponse;
import com.onedigit.utah.model2.integration.kucoin.rest.KucoinRestChain;
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
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    protected Mono<? extends RestResponse> getPrices() {
        log.info("Initiate getPrices call from kucoin");
        return get(KUCOIN_API_REST_GET_TICKERS, KucoinRestResponse.class);
    }

    @Override
    protected Mono<? extends RestResponse> getAvailability() {
        log.info("Initiate getAvailability call from kucoin");
        return get(KUCOIN_API_REST_GET_CURRENCY_LIST, KucoinRestResponse.class);
    }

    @Override
    public List<List<SpreadDTO>> populateSpreads(RestResponse response) {
        log.debug("response from kucoin");
        List<List<SpreadDTO>> overallSpreads = new ArrayList<>();
        ((KucoinRestResponse) response).getData().get(0).getTickerList().stream()
                .filter(ticker -> StringUtils.endsWith(ticker.getSymbol(), "-USDT"))
                .forEach(ticker -> {
                    String tt = StringUtils.substringBefore(ticker.getSymbol(), "-USDT");
                    BigDecimal price = Optional.ofNullable(ticker.getLast()).map(BigDecimal::new).orElse(BigDecimal.ZERO);
                    val coin = cache.savePrice(tt, Exchange.KUCOIN, price);
                    if (coin != null) {
                        val spreads = cache.calculateSpreads(coin);
                        if (!spreads.isEmpty()) {
                            overallSpreads.add(spreads);
                        }
                    }
                });
        return overallSpreads;
    }

    @Override
    public RestResponse populateAvailability(RestResponse response) {
        ((KucoinRestResponse) response).getData().stream()
                .filter(currency -> cache.hasPricesFor(currency.getCurrency()))
                .forEach(currency -> {
                    List<KucoinRestChain> chains;
                    if ((chains = currency.getChains()) != null) {
                        List<NetworkAvailabilityDTO> naDTOs = chains.stream().map(chain ->
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
                    }
                });
        return response;
    }
}


