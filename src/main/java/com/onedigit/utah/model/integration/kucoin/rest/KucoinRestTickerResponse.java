package com.onedigit.utah.model.integration.kucoin.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.onedigit.utah.model.integration.common.RestResponse;
import lombok.Value;

@Value
public class KucoinRestTickerResponse implements RestResponse {
    @JsonProperty("data")
    KucoinRestData data;

    String code;
}
