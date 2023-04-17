package com.onedigit.utah.model.api.bybit.rest;

import com.onedigit.utah.model.api.common.RestResponse;
import com.onedigit.utah.util.JsonSerializable;
import lombok.Value;

@Value
public class BybitRestResponse extends JsonSerializable implements RestResponse {
    BybitRestResult result;
    Integer retCode;
    String retMsg;
    Long time;
}
