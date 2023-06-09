package com.onedigit.utah.model.api.kucoin.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.onedigit.utah.model.api.common.RestResponse;
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
