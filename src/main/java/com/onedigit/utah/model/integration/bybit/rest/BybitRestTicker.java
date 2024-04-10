package com.onedigit.utah.model.integration.bybit.rest;

import lombok.Value;

@Value
public class BybitRestTicker {
    String lastPrice;
    String symbol;
}
