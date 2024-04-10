package com.onedigit.utah.model2.integration.mexc.rest;

import com.onedigit.utah.model2.integration.common.RestResponse;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.List;

@Value
public class MexcRestResponse implements RestResponse {
    List<MexcRestResponseTickerObject> tickers;
    List<MexcRestResponseCoinObject> coins;


}
