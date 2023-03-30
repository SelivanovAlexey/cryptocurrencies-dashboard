package com.onedigit.utah.service;

import com.onedigit.utah.model.Exchange;
import com.onedigit.utah.model.SpreadDTO;
import com.onedigit.utah.model.CoinDTO;
import com.onedigit.utah.service.event.PriceChangeEventProcessor;

import com.onedigit.utah.util.PropertiesProvider;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class MarketLocalCache {

    private static final Map<String, CoinDTO> coinMap = new HashMap<>();

    //TODO: костыль сука бесит
    @Autowired
    private PropertiesProvider staticPropertiesProvider;

    private static PropertiesProvider propertiesProvider;

    @PostConstruct
    private void initPropertiesProvider(){
        propertiesProvider = this.staticPropertiesProvider;
    }

    private MarketLocalCache() {
    }

    public static List<CoinDTO> getAllExchangesData() {
        List<CoinDTO> resultList = new ArrayList<>(coinMap.values());
        resultList.forEach(MarketLocalCache::fillSpreads);
        return resultList;
    }

    public static List<SpreadDTO> calculateSpreads(Map<Exchange, BigDecimal> map) {
        List<SpreadDTO> spreads = new ArrayList<>();
        for (Map.Entry<Exchange, BigDecimal> entry : map.entrySet()) {
            Exchange exchange = entry.getKey();
            BigDecimal price = entry.getValue();
            for (Map.Entry<Exchange, BigDecimal> innerEntry : map.entrySet()) {
                Exchange cExchange = innerEntry.getKey();
                if (exchange.equals(cExchange)) continue;
                BigDecimal cPrice = innerEntry.getValue();
                Double diff = cPrice.subtract(price).divide(price, 5, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue();
                SpreadDTO spread = new SpreadDTO(exchange, cExchange, diff);
                spreads.add(spread);
            }
        }
        return spreads;
    }

    private static void fillSpreads(CoinDTO coin) {
        coin.setSpreads(calculateSpreads(coin.getPriceToExchange()));
    }

    public static CoinDTO getTickerInfo(String ticker) {
        CoinDTO ti = coinMap.get(ticker);
        if (ti == null) {
            ti = new CoinDTO(ticker, new TickerInfoMap(ticker));
            coinMap.put(ticker, ti);
        }
        return ti;
    }

    @Slf4j
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
            if (!value.equals(price) && putCondition(ticker)) {
                price = super.put(key, value);
                CoinDTO coinUpdate = prepareResponse(key, value);
                PriceChangeEventProcessor.publish(coinUpdate);
            }
            return price;
        }

        private boolean putCondition(String ticker) {
            boolean condition = true;
            condition &= propertiesProvider.getIncludeTickers().get(0).isEmpty() ? true : propertiesProvider.getIncludeTickers().contains(ticker);
            return condition;
        }

        private CoinDTO prepareResponse(Exchange exchange, BigDecimal price) {
            TickerInfoMap cachedMap = new TickerInfoMap(this, ticker);
            List<SpreadDTO> spreads = MarketLocalCache.calculateSpreads(cachedMap);
            return new CoinDTO(ticker, Map.of(exchange, price), spreads);
        }
    }
}
