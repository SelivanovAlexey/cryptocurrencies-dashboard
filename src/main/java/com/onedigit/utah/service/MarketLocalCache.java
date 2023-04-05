package com.onedigit.utah.service;

import com.onedigit.utah.model.Exchange;
import com.onedigit.utah.model.SpreadDTO;
import com.onedigit.utah.model.CoinDTO;
import com.onedigit.utah.service.event.PriceChangeEventProcessor;

import com.onedigit.utah.util.PropertiesProvider;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

//TODO: check chain networks availability to deposit and withdraw
@Service
public class MarketLocalCache {

    private static final Map<String, CoinDTO> coinMap = new ConcurrentHashMap<>();

    //TODO: костыль сука бесит
    @Autowired
    private PropertiesProvider staticPropertiesProvider;

    private static PropertiesProvider propertiesProvider;

    public static void put(String ticker, Exchange exchange, BigDecimal price) {
        CoinDTO dto = getTickerInfo(ticker);
        if (dto != null) {
            dto.getPriceToExchange().put(exchange, price);
        }
    }

    @PostConstruct
    private void initPropertiesProvider() {
        propertiesProvider = this.staticPropertiesProvider;
    }

    private MarketLocalCache() {
    }

    public static List<CoinDTO> getAllExchangesData() {
        List<CoinDTO> resultList = new ArrayList<>(coinMap.values());
        resultList.forEach(MarketLocalCache::fillSpreads);
        return resultList.stream().filter(coindto -> !coindto.getSpreads().isEmpty()).collect(Collectors.toList());
    }

    public static List<SpreadDTO> calculateSpreads(TickerInfoMap map) {
        List<SpreadDTO> spreads = new ArrayList<>();
        for (Map.Entry<Exchange, BigDecimal> entry : map.entrySet()) {
            Exchange exchange = entry.getKey();
            BigDecimal price = entry.getValue();
            if (price.compareTo(BigDecimal.ZERO) == 0) continue;
            for (Map.Entry<Exchange, BigDecimal> innerEntry : map.entrySet()) {
                Exchange cExchange = innerEntry.getKey();
                if (exchange.equals(cExchange)) continue;
                BigDecimal cPrice = innerEntry.getValue();
                Double diff = cPrice.subtract(price).divide(price, 3, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue();
                SpreadDTO spread = new SpreadDTO(exchange, cExchange, diff);
                spreads.add(spread);
            }
        }
        spreads.sort(Comparator.comparing(SpreadDTO::getDiff).reversed());
        return spreads.stream().limit(3).collect(Collectors.toList());
    }

    private static void fillSpreads(CoinDTO coin) {
        coin.setSpreads(calculateSpreads((TickerInfoMap) coin.getPriceToExchange()));
    }

    private static CoinDTO getTickerInfo(String ticker) {
        CoinDTO ti = coinMap.get(ticker);
        if (ti == null && putCondition(ticker)) {
            ti = new CoinDTO(ticker, new TickerInfoMap(ticker));
            coinMap.put(ticker, ti);
        }
        return ti;
    }

    /**
     * @param ticker Coin ticker
     * @return true for storing in cache
     */

    private static boolean putCondition(String ticker) {
        boolean condition = true;
        condition &= !(ticker.endsWith("2S") || ticker.endsWith("3S") || ticker.endsWith("5S") || ticker.endsWith("10S"));
        condition &= !(ticker.endsWith("2L") || ticker.endsWith("3L") || ticker.endsWith("5L") || ticker.endsWith("10L"));
        condition &= propertiesProvider.getIncludeTickers().get(0).isEmpty() ? true : propertiesProvider.getIncludeTickers().contains(ticker);
        return condition;
    }

    static class TickerInfoMap extends HashMap<Exchange, BigDecimal> {
        private final String ticker;

        public TickerInfoMap(TickerInfoMap tickerInfoMap, String ticker) {
            super(tickerInfoMap);
            this.ticker = ticker;
        }

        public TickerInfoMap(String ticker) {
            this.ticker = ticker;
        }

        @Override
        public BigDecimal put(Exchange key, BigDecimal value) {
            BigDecimal price = this.get(key);
            if (!value.equals(price)) {
                price = super.put(key, value);
                if (PriceChangeEventProcessor.getListener() != null) {
                    CoinDTO coinUpdate = prepareResponse(key, value);
                    if (!coinUpdate.getSpreads().isEmpty())
                        PriceChangeEventProcessor.publish(coinUpdate);
                }
            }
            return price;
        }

        private CoinDTO prepareResponse(Exchange exchange, BigDecimal price) {
            TickerInfoMap cachedMap = new TickerInfoMap(this, ticker);
            List<SpreadDTO> spreads = MarketLocalCache.calculateSpreads(cachedMap);
            return new CoinDTO(ticker, Map.of(exchange, price), spreads);
        }
    }
}
