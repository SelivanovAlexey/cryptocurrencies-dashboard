package com.onedigit.utah.model.api.kucoin.ws;

import com.onedigit.utah.util.JsonSerializable;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@EqualsAndHashCode(callSuper = true)
@Value
@Builder
@Jacksonized
public class KucoinWsData extends JsonSerializable {
    String sequence;
    String bestAsk;
    String size;
    String bestBidSize;
    String price;
    String bestAskSize;
    String bestBid;
}