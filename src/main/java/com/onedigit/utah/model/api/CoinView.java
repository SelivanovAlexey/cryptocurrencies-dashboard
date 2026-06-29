package com.onedigit.utah.model.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.onedigit.utah.model.Exchange;
import com.onedigit.utah.model.NetworkAvailability;
import com.onedigit.utah.model.Spread;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record CoinView(
        String ticker,
        Map<Exchange, BigDecimal> prices,
        List<Spread> spreads,
        Map<Exchange, List<NetworkAvailability>> availability
) {
    public CoinView(String ticker, List<Spread> spreads) {
        this(ticker, Map.of(), spreads, Map.of());
    }
}
