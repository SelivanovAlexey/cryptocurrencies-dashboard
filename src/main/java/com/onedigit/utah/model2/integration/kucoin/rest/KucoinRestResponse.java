package com.onedigit.utah.model2.integration.kucoin.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.onedigit.utah.model2.integration.common.RestResponse;
import lombok.Value;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY;

@Value
public class KucoinRestResponse implements RestResponse {
    @JsonFormat(with = ACCEPT_SINGLE_VALUE_AS_ARRAY)
    List<KucoinRestData> data;
    String code;
}
