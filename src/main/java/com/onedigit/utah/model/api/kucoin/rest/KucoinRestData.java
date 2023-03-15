package com.onedigit.utah.model.api.kucoin.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.List;

@Value
public class KucoinRestData {
    Long time;
    @JsonProperty("ticker")
    List<KucoinRestTicker> tickerList;
    String token;
    List<KucoinRestInstanceServer> instanceServers;

}
