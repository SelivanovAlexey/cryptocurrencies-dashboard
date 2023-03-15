package com.onedigit.utah.model.api.bybit;

import lombok.Value;

@Value
public class BybitRestResponse {
    BybitRestResult result;
    Integer retCode;
    String retMsg;
    Object retExtInfo;
    Long time;
}
