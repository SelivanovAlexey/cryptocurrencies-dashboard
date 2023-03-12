package com.onedigit.utah.service;

import com.onedigit.utah.model.CoinDTO;

import java.util.ArrayList;
import java.util.List;

public class MarketLocalCache {
    private static final MarketLocalCache cacheInstance = new MarketLocalCache();;
    private static final List<CoinDTO> coinList = new ArrayList<>();

    private MarketLocalCache() {
    }

    public static List<CoinDTO> getCache(){
        return coinList;
    }

    //TODO: useless?
    public static MarketLocalCache getInstance(){
        return cacheInstance;
    }





}
