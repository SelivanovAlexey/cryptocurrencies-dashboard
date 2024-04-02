package com.onedigit.utah.model2.integration.mexc.rest;

import com.onedigit.utah.util.JsonSerializable;
import lombok.EqualsAndHashCode;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@Value
public class MexcRestResponseTickerObject extends JsonSerializable {
    String symbol;
    String price;
}
