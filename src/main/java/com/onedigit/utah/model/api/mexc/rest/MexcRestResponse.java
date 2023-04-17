package com.onedigit.utah.model.api.mexc.rest;

import com.onedigit.utah.model.api.common.RestResponse;
import com.onedigit.utah.util.JsonSerializable;
import lombok.Value;

import java.util.List;

@Value
public class MexcRestResponse extends JsonSerializable implements RestResponse {
    List<MexcRestResponseObject> tickers;
}
