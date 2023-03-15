package com.onedigit.utah.model.api.kucoin.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.onedigit.utah.util.JsonSerializable;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Value
public class KucoinRestData extends JsonSerializable {
    Long time;
    @JsonProperty("ticker")
    List<KucoinRestTicker> tickerList;
    String token;
    List<KucoinRestInstanceServer> instanceServers;

}
