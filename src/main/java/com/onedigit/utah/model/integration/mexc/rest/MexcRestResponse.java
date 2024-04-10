package com.onedigit.utah.model.integration.mexc.rest;

import com.onedigit.utah.model.integration.common.RestResponse;
import lombok.Value;

import java.util.List;

@Value
public class MexcRestResponse implements RestResponse {
    List<MexcRestResponseObject> tickers;
}
