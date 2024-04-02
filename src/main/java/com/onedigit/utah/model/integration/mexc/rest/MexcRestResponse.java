package com.onedigit.utah.model.integration.mexc.rest;

import com.onedigit.utah.model.integration.common.RestResponse;
import com.onedigit.utah.util.JsonSerializable;
import lombok.Value;

import java.util.List;

@Value
public class MexcRestResponse extends JsonSerializable implements RestResponse {
    List<MexcRestResponseObject> tickers;
}
