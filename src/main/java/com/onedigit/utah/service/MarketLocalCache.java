package com.onedigit.utah.service;

import com.onedigit.utah.model.Exchange;
import com.onedigit.utah.model.SpreadDTO;
import com.onedigit.utah.model.CoinDTO;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//TODO: implement sending of updated values
@Slf4j
public class MarketLocalCache {
    private static final Map<String, CoinDTO> coinMap = new HashMap<>();

    private MarketLocalCache() {}

    public static List<CoinDTO> getAllExchangesData() {
        List<CoinDTO> resultList = new ArrayList<>(coinMap.values());
        resultList.forEach(MarketLocalCache::fillSpreads);
        return resultList;
    }

    private static void fillSpreads(CoinDTO targetCoin){
        Map<Exchange, BigDecimal> map = targetCoin.getPriceToExchange();
        List<SpreadDTO> spreads = new ArrayList<>();
        for(Map.Entry<Exchange, BigDecimal> entry: map.entrySet()) {
            Exchange exchange = entry.getKey();
            BigDecimal price = entry.getValue();
            for(Map.Entry<Exchange, BigDecimal> innerEntry: map.entrySet()) {
                Exchange cExchange = innerEntry.getKey();
                if (exchange.equals(cExchange)) continue;
                BigDecimal cPrice = innerEntry.getValue();
                Double diff = cPrice.subtract(price).divide(price, 5, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue();
                SpreadDTO spread = new SpreadDTO(exchange, cExchange, diff);
                spreads.add(spread);
            }
        }
        targetCoin.setSpreads(spreads);
    }

    public static CoinDTO getTickerInfo(String ticker) {
        CoinDTO ti = coinMap.get(ticker);
        if (ti == null){
            ti = new CoinDTO(ticker, new HashMap<>());
            coinMap.put(ticker, ti);
        }
        return ti;
    }
}
