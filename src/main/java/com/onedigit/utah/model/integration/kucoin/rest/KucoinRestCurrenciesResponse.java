package com.onedigit.utah.model.integration.kucoin.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.List;

@Value
public class KucoinRestCurrenciesResponse {
    String code;

    @JsonProperty("data")
    List<KucoinRestData> data;
}
