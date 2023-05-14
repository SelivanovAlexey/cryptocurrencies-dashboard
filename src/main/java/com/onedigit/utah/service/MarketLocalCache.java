package com.onedigit.utah.service;

import com.onedigit.utah.model.Exchange;
import com.onedigit.utah.model.NetworkAvailabilityDTO;
import com.onedigit.utah.model.SpreadDTO;
import com.onedigit.utah.model.CoinDTO;
import com.onedigit.utah.model.view.VerboseView;
import com.onedigit.utah.model.view.SpreadView;
import com.onedigit.utah.service.event.CacheChangedEventProcessor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

//TODO: test copy of coin map array
@Service
public class MarketLocalCache {

    private static final Map<String, CoinDTO> coinMap = new ConcurrentHashMap<>();

    private static List<String> includeTickers;

    //TODO: WA - to fix
    @Value("#{'${api.includeTickers}'.split(',')}")
    public void setIncludeTickers(List<String> includeTickers) {
        MarketLocalCache.includeTickers = includeTickers;
    }

    private static String tickerVerboseFlag;

    public static void savePrice(String ticker, Exchange exchange, BigDecimal price) {
        CoinDTO dto = getTickerInfo(ticker);
        if (dto == null) {
            if (putCondition(ticker)) {
                dto = new CoinDTO(ticker, new HashMap<>());
                coinMap.put(ticker, dto);
            } else return;
        }
        saveToCache(dto, exchange, price, dto.getPriceToExchange());
    }

    public static void saveAvailability(String ticker, Exchange exchange, List<NetworkAvailabilityDTO> list) {
        CoinDTO dto = getTickerInfo(ticker);
        if (dto == null) {
            if (putCondition(ticker)) {
                dto = new CoinDTO(ticker, new HashMap<>());
                coinMap.put(ticker, dto);
            } else return;
        }
        saveToCache(dto, exchange, list, dto.getNetworkAvailabilityToExchange());
    }

    @SuppressWarnings("rawtypes, unchecked")
    private static void saveToCache(CoinDTO dto, Exchange exchange, Object obj, Map map) {
        // check if the same value is present in the cache
        Object oldObj = map.get(exchange);
        if (!obj.equals(oldObj)) {
            map.put(exchange, obj);
            CoinDTO updatedCoin = prepareResponse(dto);
            if (!updatedCoin.getSpreads().isEmpty()){
                CacheChangedEventProcessor.publish(updatedCoin);
            }
        }
    }

    @SuppressWarnings("rawtypes, unchecked")
    private static CoinDTO prepareResponse(CoinDTO dto) {
        Map cachedMap = dto.getPriceToExchange();
        List<SpreadDTO> spreads = MarketLocalCache.calculateSpreads(cachedMap);
        CoinDTO resultCoin;
        if (dto.getTicker().equals(tickerVerboseFlag)) {
            resultCoin = new VerboseView(dto.getTicker(), dto.getPriceToExchange(), spreads, dto.getNetworkAvailabilityToExchange());
        } else {
            resultCoin = new SpreadView(dto.getTicker(), spreads);
        }
        return resultCoin;
    }

    public static void enableVerboseInfo(String ticker) {
        tickerVerboseFlag = ticker;
    }

    private MarketLocalCache() {
    }

    public static List<SpreadView> getAllExchangesData() {
        List<CoinDTO> resultList = new ArrayList<>(coinMap.values());
        resultList.forEach(MarketLocalCache::fillSpreads);
        return resultList.stream().filter(coindto -> !coindto.getSpreads().isEmpty())
                .map(coinDTO -> new SpreadView(coinDTO.getTicker(), coinDTO.getSpreads()))
                .collect(Collectors.toList());
    }

    public static List<SpreadDTO> calculateSpreads(Map<Exchange, BigDecimal> map) {
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
        coin.setSpreads(calculateSpreads(coin.getPriceToExchange()));
    }


    private static boolean putCondition(String ticker) {
        boolean condition = true;
        condition &= !(ticker.endsWith("2S") || ticker.endsWith("3S") || ticker.endsWith("5S") || ticker.endsWith("10S"));
        condition &= !(ticker.endsWith("2L") || ticker.endsWith("3L") || ticker.endsWith("5L") || ticker.endsWith("10L"));
        condition &= includeTickers.get(0).isEmpty() ? true : includeTickers.contains(ticker);
        return condition;
    }

    public static CoinDTO getTickerInfo(String ticker) {
        return coinMap.get(ticker);
    }

    public static boolean isTickerExists(String ticker) {
        return coinMap.containsKey(ticker);
    }
}
