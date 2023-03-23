package com.onedigit.utah.model.api.bybit.ws;

import com.onedigit.utah.util.JsonSerializable;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@EqualsAndHashCode(callSuper = true)
@Value
@Builder
@Jacksonized
public class BybitWsData extends JsonSerializable {
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