package com.onedigit.utah.integration.impl.rest;

import com.onedigit.utah.config.RestProperties;
import com.onedigit.utah.integration.impl.helpers.BybitApiHelper;
import com.onedigit.utah.integration.impl.BaseExchangeAdapter;
import com.onedigit.utah.model.Exchange;
import com.onedigit.utah.model.NetworkAvailability;
import com.onedigit.utah.model.events.AvailabilityUpdate;
import com.onedigit.utah.model.events.PriceUpdate;
import com.onedigit.utah.model.integration.bybit.rest.BybitRestResponse;
import com.onedigit.utah.model.integration.bybit.rest.BybitRestResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.*;

import static com.onedigit.utah.constants.ApiConstants.*;

@Slf4j
@Service
public class BybitAdapterImpl extends BaseExchangeAdapter {

    private final BybitApiHelper apiHelper;
    private final RestProperties restProperties;

    public BybitAdapterImpl(@Qualifier("bybitRestApiClient") WebClient bybitRestApiClient, BybitApiHelper apiHelper, RestProperties restProperties) {
        this.apiHelper = apiHelper;
        this.restProperties = restProperties;
        this.webClient = bybitRestApiClient;
    }

    /**
     * Retrieves only -USDT tickers
     */
    @Override
    public Flux<List<PriceUpdate>> priceUpdates() {
        return
                getWithRepeat(uri -> uri.path(BYBIT_API_REST_GET_TICKERS).queryParams(MultiValueMap.fromSingleValue(Map.of("category", "spot"))),
                        BybitRestResponse.class,
                        Duration.ofMillis(restProperties.priceCallsIntervalMs()), exchangeRetryPolicy(log))
                .map(this::toPriceUpdate);
    }

    private List<PriceUpdate> toPriceUpdate(BybitRestResponse response) {
        return Optional.ofNullable(response)
                .map(BybitRestResponse::getResult)
                .map(BybitRestResult::getTickers)
                .orElseGet(() -> logParseErrors(log, response))
                .stream()
                .filter(ticker ->  ticker.getSymbol() != null && ticker.getSymbol().endsWith("USDT") && ticker.getLastPrice() != null)
                .map(ticker -> {
                    String tt = StringUtils.substringBefore(ticker.getSymbol(), "USDT");
                    return new PriceUpdate(tt, exchange(), ticker.getLastPrice());
                })
                .toList();
    }

    @Override
    public Exchange exchange() {
        return Exchange.bybit;
    }

    @Override
    public Flux<List<AvailabilityUpdate>> availabilityUpdates() {

        return getWithRepeat(uri -> uri.path(BYBIT_API_REST_GET_COIN_INFO),
                httpHeaders -> httpHeaders.addAll(apiHelper.applySignedHeaders()),
                BybitRestResponse.class,
                Duration.ofMillis(restProperties.availabilityCallsIntervalMs()), exchangeRetryPolicy(log))
                .map(this::toAvailabilityUpdate);
    }

    public List<AvailabilityUpdate> toAvailabilityUpdate(BybitRestResponse response) {
        return Optional.ofNullable(response)
                .map(BybitRestResponse::getResult)
                .map(BybitRestResult::getRows)
                .orElseGet(() -> logParseErrors(log, response))
                .stream()
                .map(row -> new AvailabilityUpdate(
                        row.getCoin(),
                        exchange(),
                        Optional.ofNullable(row.getChains()).orElseGet(List::of).stream()
                                .map(chain -> NetworkAvailability.builder()
                                        .networkChainName(chain.getChain())
                                        .networkChainType(chain.getChainType())
                                        .isDepositAvailable("1".equals(chain.getChainDeposit()))
                                        .isWithdrawAvailable("1".equals(chain.getChainWithdraw()))
                                        .build())
                                .toList()))
                .toList();
    }
}
