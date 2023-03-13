package com.onedigit.utah.service;

import com.onedigit.utah.model.Exchange;
import com.onedigit.utah.util.CumulativeHashMap;

import java.util.*;

public class MarketLocalCache {
    private static final Map<Exchange, CumulativeHashMap<String, Double>> coinMap = new HashMap<>();

    static {
        coinMap.put(Exchange.KUCOIN, new CumulativeHashMap<>());
    }

    private MarketLocalCache() {}

    public static Map<Exchange, CumulativeHashMap<String, Double>> getAllExchangesData(){
        return coinMap;
    }

    public static void flush() {
        coinMap.forEach((exchange, map)-> map.clear());
    }
}
