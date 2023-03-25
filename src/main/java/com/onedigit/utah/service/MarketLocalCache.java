package com.onedigit.utah.service;

import com.onedigit.utah.model.CoinDTO;
import com.onedigit.utah.model.Exchange;
import com.onedigit.utah.model.SpreadDTO;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

//TODO: to test performance in cache and choose correct model: flat or mapped(MarketLocalCache2)
//TODO: implement sending of updated values
@Slf4j
public class MarketLocalCache {
    private static final ConcurrentLinkedQueue<CoinDTO> coinMap = new ConcurrentLinkedQueue<>();

    private MarketLocalCache() {}

    public static void put(String ticker, Exchange exchange, BigDecimal price) {
        coinMap.stream().filter(coin -> coin.getTicker().equals(ticker) && coin.getExchange().equals(exchange)).findFirst().ifPresentOrElse(coin -> {
            if (!coin.getPrice().equals(price)) {
                coin.setPrice(price);
            }
        }, () -> {
            CoinDTO newCoin = new com.onedigit.utah.model.CoinDTO(ticker, exchange, price);
            coinMap.add(newCoin);
        });
    }

    public static List<CoinDTO> getAllExchangesData() {
        List<CoinDTO> resultList = coinMap.stream().toList();
        resultList.forEach(coin -> fillSpreads(coin, resultList));
        return resultList;
    }

    private static void fillSpreads(CoinDTO targetCoin, List<CoinDTO> coinList){
        List<SpreadDTO> spreadList = coinList.stream()
                .filter(coinDTO -> coinDTO.getTicker().equals(targetCoin.getTicker()))
                .filter(coinDTO -> !coinDTO.getExchange().equals(targetCoin.getExchange()))
                .map(coinDTO -> {
                    Double diff = coinDTO.getPrice()
                            .subtract(targetCoin.getPrice())
                            .divide(targetCoin.getPrice(), 5, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100)).doubleValue();
                    return new SpreadDTO(targetCoin.getExchange(), coinDTO.getExchange(), diff);
                }).collect(Collectors.toList());
        targetCoin.setSpreadList(spreadList);
    }
}
