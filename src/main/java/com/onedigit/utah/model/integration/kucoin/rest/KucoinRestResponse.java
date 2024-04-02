package com.onedigit.utah.model.integration.kucoin.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.onedigit.utah.model.integration.common.RestResponse;
import com.onedigit.utah.util.JsonSerializable;
import lombok.EqualsAndHashCode;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@Value
public class KucoinRestResponse extends JsonSerializable implements RestResponse {
    @JsonProperty("data")
    KucoinRestData data;

    String code;
}
