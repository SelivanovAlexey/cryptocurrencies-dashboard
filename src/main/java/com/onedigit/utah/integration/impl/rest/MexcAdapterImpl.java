package com.onedigit.utah.integration.impl.rest;

import com.onedigit.utah.config.IntegrationProperties;
import com.onedigit.utah.config.RestProperties;
import com.onedigit.utah.integration.impl.BaseExchangeAdapter;
import com.onedigit.utah.integration.impl.helpers.MexcApiHelper;
import com.onedigit.utah.model.Exchange;
import com.onedigit.utah.model.NetworkAvailability;
import com.onedigit.utah.model.events.AvailabilityUpdate;
import com.onedigit.utah.model.events.PriceUpdate;
import com.onedigit.utah.model.integration.mexc.rest.MexcRestResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.onedigit.utah.constants.ApiConstants.*;

@Slf4j
@Service
public class MexcAdapterImpl extends BaseExchangeAdapter {
    private final MexcApiHelper apiHelper;
    private final RestProperties restProperties;
    private final String apiKey;

    public MexcAdapterImpl(WebClient mexcRestApiClient, MexcApiHelper apiHelper, RestProperties restProperties, IntegrationProperties integrationProperties) {
        this.apiHelper = apiHelper;
        this.restProperties = restProperties;
        this.apiKey = integrationProperties.mexcApiKeyValue();
        this.webClient = mexcRestApiClient;
    }

    @Override
    public Flux<List<PriceUpdate>> priceUpdates() {
        return getWithRepeat(uri -> uri.path(MEXC_API_REST_GET_TICKERS),
                MexcRestResponse[].class,
                Duration.ofMillis(restProperties.priceCallsIntervalMs()), exchangeRetryPolicy(log))
                .map(this::toPriceUpdate);
    }

    private List<PriceUpdate> toPriceUpdate(MexcRestResponse[] response) {
        return Optional.ofNullable(response)
                .map(Arrays::asList)
                .orElseGet(() -> logParseErrors(log, response))
                .stream()
                .filter(ticker -> ticker.getSymbol() != null && ticker.getSymbol().endsWith("USDT") && ticker.getPrice() != null)
                .map(ticker -> {
                    String tt = StringUtils.substringBefore(ticker.getSymbol(), "USDT");
                    return new PriceUpdate(tt, exchange(), ticker.getPrice());
                })
                .toList();
    }

    @Override
    public Exchange exchange() {
        return Exchange.mexc;
    }

    @Override
    public Flux<List<AvailabilityUpdate>> availabilityUpdates() {
        return getWithRepeat(
                uri -> uri.path(MEXC_API_REST_GET_CURRENCY_INFO).queryParams(CollectionUtils.toMultiValueMap(apiHelper.signQueryString())),
                httpHeaders -> httpHeaders.add("X-MEXC-APIKEY", apiKey),
                MexcRestResponse[].class,
                Duration.ofMillis(restProperties.availabilityCallsIntervalMs()), exchangeRetryPolicy(log))
                .map(this::toAvailabilityUpdate);
    }

    private List<AvailabilityUpdate> toAvailabilityUpdate(MexcRestResponse[] response) {
        return Optional.ofNullable(response)
                .map(Arrays::asList)
                .orElseGet(() -> logParseErrors(log, response))
                .stream()
                .map(coinObj -> new AvailabilityUpdate(
                        coinObj.getCoin(),
                        exchange(),
                        Optional.ofNullable(coinObj.getNetworkList()).orElseGet(List::of).stream()
                                .map(network -> NetworkAvailability.builder()
                                        .networkChainName(parseNetwork(network.getNetwork()))
                                        .isDepositAvailable(network.isDepositEnable())
                                        .isWithdrawAvailable(network.isWithdrawEnable())
                                        .build())
                                .toList()))
                .toList();
    }

    private String parseNetwork(String fullNetworkName){
        String networkName = StringUtils.substringBetween(fullNetworkName, "(", ")");
        return networkName != null ? networkName : fullNetworkName;
    }
}