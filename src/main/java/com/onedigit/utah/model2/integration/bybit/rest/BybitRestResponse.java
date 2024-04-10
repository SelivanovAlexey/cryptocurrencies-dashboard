package com.onedigit.utah.model2.integration.bybit.rest;

import com.onedigit.utah.model2.integration.common.RestResponse;
import lombok.Value;

@Value
public class BybitRestResponse implements RestResponse {
    BybitRestResult result;
    Integer retCode;
    String retMsg;
    Long time;
}
