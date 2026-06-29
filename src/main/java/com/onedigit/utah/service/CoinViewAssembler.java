package com.onedigit.utah.service;

import com.onedigit.utah.model.Coin;
import com.onedigit.utah.model.Exchange;
import com.onedigit.utah.model.NetworkAvailability;
import com.onedigit.utah.model.api.CoinView;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Component
public class CoinViewAssembler {

    private final SpreadCalculator spreadCalculator;

    public CoinViewAssembler(SpreadCalculator spreadCalculator) {
        this.spreadCalculator = spreadCalculator;
    }

    public CoinView verbose(Coin dto) {
        Map<Exchange, BigDecimal> prices = Map.copyOf(dto.getPrices());
        Map<Exchange, List<NetworkAvailability>> availability = Map.copyOf(dto.getAvailability());
        return new CoinView(dto.getTicker(), prices, spreadCalculator.calculateSpreads(prices), availability);
    }
}