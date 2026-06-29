package com.onedigit.utah.service;

import com.onedigit.utah.config.CacheProperties;
import com.onedigit.utah.model.Coin;

import com.onedigit.utah.model.Exchange;
import com.onedigit.utah.model.events.AvailabilityUpdate;
import com.onedigit.utah.model.events.PriceUpdate;
import com.onedigit.utah.model.events.UpdateEvent;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MarketLocalCache {

    private final Map<String, Coin> coinMap = new ConcurrentHashMap<>();
    private final List<String> includedTickers;

    private MarketLocalCache(CacheProperties cacheProperties) {
        this.includedTickers = cacheProperties.includedTickers();
    }

    /**
     * Pattern matched cache update
     * @param update event from adapters
     * @return {@code true} if obtained value changed the cache value
     */
    public boolean update(UpdateEvent update) {
        if (!isAllowed(update.ticker())) return false;
        Coin dto = coinMap.computeIfAbsent(update.ticker(), Coin::new);
        return switch (update) {
            case PriceUpdate p -> put(dto.getPrices(), p.exchange(), p.price());
            case AvailabilityUpdate a -> put(dto.getAvailability(), a.exchange(), a.availability());
        };
    }

    private <V> boolean put(Map<Exchange, V> map, Exchange exchange, V value) {
        return !value.equals(map.put(exchange, value));
    }

    public Optional<Coin> find(String ticker) {
        return Optional.ofNullable(coinMap.get(ticker));
    }

    public Collection<Coin> snapshot() {
        return List.copyOf(coinMap.values());
    }

    private boolean isAllowed(String ticker) {
        boolean condition = true;
        condition &= !(ticker.endsWith("2S") || ticker.endsWith("3S") || ticker.endsWith("5S") || ticker.endsWith("10S"));
        condition &= !(ticker.endsWith("2L") || ticker.endsWith("3L") || ticker.endsWith("5L") || ticker.endsWith("10L"));
        condition &= includedTickers.isEmpty() || includedTickers.contains(ticker);
        return condition;
    }
}