package com.onedigit.utah.model.integration.bybit.rest;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class BybitRestTicker{
    BigDecimal lastPrice;
    String symbol;
}
