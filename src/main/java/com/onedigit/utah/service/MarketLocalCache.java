package com.onedigit.utah.service;

import com.onedigit.utah.model.CoinDTO;
import com.onedigit.utah.model.Exchange;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

//TODO: to test approach with the queue
public class MarketLocalCache {
    private static final List<CoinDTO> coinMap = new ArrayList<>();


    private MarketLocalCache() {
    }

    public static List<CoinDTO> getAllExchangesData() {
        return coinMap;
    }

    static ConcurrentLinkedQueue<CoinDTO> valuesToUpdate = new CoinsQueue();

    public static List<CoinDTO> getValuesToUpdate() {
        List<CoinDTO> newList = new ArrayList<>();
        valuesToUpdate.forEach((el) -> newList.add(valuesToUpdate.poll()));
        return newList;
    }

    //TODO: to add spread calculation
    public static void put(String ticker, Exchange exchange, Double price) {
        coinMap.stream().filter(coin -> coin.getTicker().equals(ticker) && coin.getExchange().equals(exchange)).findFirst().ifPresentOrElse(coin -> {
            if (!coin.getPrice().equals(price)) {
                coin.setPrice(price);
                valuesToUpdate.offer(coin);
            }
        }, () -> {
            CoinDTO newCoin = new com.onedigit.utah.model.CoinDTO(ticker, exchange, price);
            coinMap.add(newCoin);
            valuesToUpdate.offer(newCoin);
        });
    }

    //TODO: is there is really needed to go through full list of values?
    static class CoinsQueue extends ConcurrentLinkedQueue<CoinDTO>{
        @Override
        public boolean offer(CoinDTO coinDTO) {
            if (!this.contains(coinDTO)) return super.offer(coinDTO);
            return true;
        }
    }


}
