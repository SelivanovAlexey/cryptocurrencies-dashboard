package com.onedigit.utah.model;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public final class Coin {
    private final String ticker;
    private final Map<Exchange, BigDecimal> prices = new ConcurrentHashMap<>();
    private final Map<Exchange, List<NetworkAvailability>> availability = new ConcurrentHashMap<>();

    public Coin(@NonNull String ticker) {
        this.ticker = ticker;
    }
}
