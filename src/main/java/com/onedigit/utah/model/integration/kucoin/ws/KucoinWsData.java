package com.onedigit.utah.model.integration.kucoin.ws;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class KucoinWsData {
    String sequence;
    String bestAsk;
    String size;
    String bestBidSize;
    String price;
    String bestAskSize;
    String bestBid;
}