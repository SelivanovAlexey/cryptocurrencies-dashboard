package com.onedigit.utah.model2.integration.kucoin.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.onedigit.utah.model2.integration.common.RestResponse;
import com.onedigit.utah.util.JsonSerializable;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Value
public class KucoinRestResponse extends JsonSerializable implements RestResponse {
    @JsonProperty("data")
    List<KucoinRestData> data;
    String code;
}
