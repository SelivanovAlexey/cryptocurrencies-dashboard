package com.onedigit.utah.model.api.bybit;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.List;

@Value
public class BybitRestResult {
    @JsonProperty("list")
    List<BybitRestTicker> tickers;
    String category;
}
