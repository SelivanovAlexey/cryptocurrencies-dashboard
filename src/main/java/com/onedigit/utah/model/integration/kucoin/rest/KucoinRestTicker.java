package com.onedigit.utah.model.integration.kucoin.rest;

import lombok.EqualsAndHashCode;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class KucoinRestTicker {
    String symbol;
    BigDecimal last;
}
