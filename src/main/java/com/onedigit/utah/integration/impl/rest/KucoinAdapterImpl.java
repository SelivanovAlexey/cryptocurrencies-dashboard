package com.onedigit.utah.integration.impl.rest;

import com.onedigit.utah.config.RestProperties;
import com.onedigit.utah.integration.impl.BaseExchangeAdapter;
import com.onedigit.utah.model.Exchange;
import com.onedigit.utah.model.NetworkAvailability;
import com.onedigit.utah.model.events.AvailabilityUpdate;
import com.onedigit.utah.model.events.PriceUpdate;
import com.onedigit.utah.model.integration.kucoin.rest.KucoinRestCurrenciesResponse;
import com.onedigit.utah.model.integration.kucoin.rest.KucoinRestData;
import com.onedigit.utah.model.integration.kucoin.rest.KucoinRestTickerResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static com.onedigit.utah.constants.ApiConstants.*;

@Slf4j
@Service
public class KucoinAdapterImpl extends BaseExchangeAdapter {
    private final RestProperties restProperties;


    public KucoinAdapterImpl(@Qualifier("kucoinRestApiClient") WebClient kucoinRestApiClient, RestProperties restProperties) {
        this.restProperties = restProperties;
        this.webClient = kucoinRestApiClient;
    }

    @Override
    public Exchange exchange() {
        return Exchange.kucoin;
    }

    @Override
    public Flux<List<PriceUpdate>> priceUpdates() {
        return getWithRepeat(uri -> uri.path(KUCOIN_API_REST_GET_TICKERS),
                KucoinRestTickerResponse.class,
                Duration.ofMillis(restProperties.priceCallsIntervalMs()), exchangeRetryPolicy(log))
                .map(this::toPriceUpdate);
    }

    private List<PriceUpdate> toPriceUpdate(KucoinRestTickerResponse response) {
        return Optional.ofNullable(response)
                .map(KucoinRestTickerResponse::getData)
                .map(KucoinRestData::getTickerList)
                .orElseGet(() -> logParseErrors(log, response))
                .stream()
                .filter(ticker -> ticker.getSymbol() != null && ticker.getSymbol().endsWith("-USDT") && ticker.getLast() != null)
                .map(ticker -> {
                    String tt = StringUtils.substringBefore(ticker.getSymbol(), "-USDT");
                    return new PriceUpdate(tt, exchange(), ticker.getLast());
                })
                .toList();
    }

    @Override
    public Flux<List<AvailabilityUpdate>> availabilityUpdates() {
        return getWithRepeat(uri -> uri.path(KUCOIN_API_REST_GET_CURRENCIES),
                KucoinRestCurrenciesResponse.class,
                Duration.ofMillis(restProperties.availabilityCallsIntervalMs()), exchangeRetryPolicy(log))
                .map(this::toAvailabilityUpdate);
    }

    private List<AvailabilityUpdate> toAvailabilityUpdate(KucoinRestCurrenciesResponse response) {
        return Optional.ofNullable(response)
                .map(KucoinRestCurrenciesResponse::getData)
                .orElseGet(() -> logParseErrors(log, response))
                .stream()
                .map(data -> new AvailabilityUpdate(
                        data.getCurrency(),
                        exchange(),
                        Optional.ofNullable(data.getChains()).orElseGet(List::of).stream()
                                .map(chain -> NetworkAvailability.builder()
                                        .networkChainName(chain.getChainName())
                                        .isDepositAvailable(chain.isDepositEnabled())
                                        .isWithdrawAvailable(chain.isWithdrawEnabled())
                                        .build())
                                .toList()))
                .toList();
    }
}


