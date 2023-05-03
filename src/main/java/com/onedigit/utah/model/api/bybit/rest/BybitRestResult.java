package com.onedigit.utah.model.api.bybit.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.onedigit.utah.util.JsonSerializable;
import lombok.Value;

import java.util.List;

@Value
public class BybitRestResult extends JsonSerializable {
    @JsonProperty("list")
    List<BybitRestTicker> tickers;
    String category;
    List<BybitRestRow> rows;
}
