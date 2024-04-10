package com.onedigit.utah.model2.integration.mexc.rest;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
public class MexcRestResponseTickerObject {
    String symbol;
    String price;
}
