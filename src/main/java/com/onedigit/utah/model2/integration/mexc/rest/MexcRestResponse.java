package com.onedigit.utah.model2.integration.mexc.rest;

import com.onedigit.utah.model2.integration.common.RestResponse;
import com.onedigit.utah.util.JsonSerializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Value
public class MexcRestResponse extends JsonSerializable implements RestResponse {
    List<MexcRestResponseTickerObject> tickers;
    List<MexcRestResponseCoinObject> coins;


}
