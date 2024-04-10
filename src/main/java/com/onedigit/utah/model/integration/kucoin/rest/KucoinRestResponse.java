package com.onedigit.utah.model.integration.kucoin.rest;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.onedigit.utah.model.integration.common.RestResponse;
import lombok.EqualsAndHashCode;
import lombok.Value;

import static com.fasterxml.jackson.annotation.JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY;

@Value
public class KucoinRestResponse implements RestResponse {
    @JsonFormat(with = ACCEPT_SINGLE_VALUE_AS_ARRAY)
    KucoinRestData data;

    String code;
}
