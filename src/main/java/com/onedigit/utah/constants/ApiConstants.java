package com.onedigit.utah.constants;

public interface ApiConstants {

    /**
     * KUCOIN EXCHANGE REGION
     */
    String KUCOIN_API_REST_BASE_URL = "https://api.kucoin.com";
    String KUCOIN_API_REST_GET_TICKERS = "/api/v1/market/allTickers";
    String KUCOIN_API_REST_GET_CURRENCIES = "/api/v3/currencies";
    String KUCOIN_API_REST_GET_CONNECT_TOKEN_URL = "/api/v1/bullet-public";
    String KUCOIN_API_WS_SPOT_URL = "wss://stream.bybit.com/v5/public/spot";
    String KUCOIN_TOPIC_MARKET_DATA = "/market/ticker:all";

    /**
     * BYBIT EXCHANGE REGION
     */
    String BYBIT_API_REST_BASE_URL = "https://api.bybit.com";
    String BYBIT_API_REST_GET_TICKERS = "/v5/market/tickers";
    String BYBIT_API_REST_GET_COIN_INFO = "/v5/asset/coin/query-info";
    String BYBIT_API_WS_SPOT_URL = "wss://stream.bybit.com/v5/public/spot";


    /**
     * MEXC EXCHANGE REGION
     */

    String MEXC_API_REST_BASE_URL = "https://api.mexc.com";
    String MEXC_API_REST_GET_TICKERS = "/api/v3/ticker/price";
    String MEXC_API_REST_GET_CURRENCY_INFO = "/api/v3/capital/config/getall";

}
