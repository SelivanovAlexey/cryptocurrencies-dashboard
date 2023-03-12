package com.onedigit.utah.service;

import com.onedigit.utah.model.CoinDTO;
import com.onedigit.utah.model.Exchange;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExchangeParser {
    public static List<CoinDTO> transformExchangeData(Exchange exchange, Map<String, String> data, List<CoinDTO> coinList){
        // initialization phase
        for (String ticker: data.keySet()) {
            coinList.add(new CoinDTO(ticker, new HashMap<>()));
        }
        for (CoinDTO coin: coinList){
            coin.getExchangesInfo().put(exchange, Double.valueOf(data.get(coin.getTicker())));
        }
        return coinList;
    }
}
