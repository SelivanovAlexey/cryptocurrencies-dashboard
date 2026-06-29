package com.onedigit.utah.model.integration.bybit.ws;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class BybitWsData{
    String symbol;
    String lastPrice;
    String highPrice24h;
    String lowPrice24h;
    String prevPrice24h;
    String volume24h;
    String turnover24h;
    String price24hPcnt;
    String usdIndexPrice;

}