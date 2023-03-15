package com.onedigit.utah.model.api.kucoin.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class KucoinRestResponse {
    @JsonProperty("data")
    KucoinRestData data;

    String code;
}
