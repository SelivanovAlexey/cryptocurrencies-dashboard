package com.onedigit.utah.constants;

public interface ApiConstants {

    /**
     * OVERALL REGION
     */
    Integer FRONTEND_UPDATE_FREQUENCY_MS = 1000;

    /**
     * KUCOIN EXCHANGE REGION
     */
    String KUCOIN_API_REST_BASE_URL = "https://api.kucoin.com";
    String KUCOIN_API_REST_GET_CONNECT_TOKEN_URL = "/api/v1/bullet-public";
    String KUCOIN_API_WS_SPOT_URL = "wss://stream.bybit.com/v5/public/spot";
    String KUCOIN_TOPIC_MARKET_DATA = "/market/ticker:all";

    /**
     * BYBIT EXCHANGE REGION
     */
    String BYBIT_API_REST_BASE_URL = "https://api.bybit.com";
    String BYBIT_API_REST_GET_TICKERS = "/v5/market/tickers";
    String BYBIT_API_WS_SPOT_URL = "wss://stream.bybit.com/v5/public/spot";
    //TODO: to remove when implement ws approach
    Integer BYBIT_REST_CALLS_FREQUENCY_MS = 0;

}
