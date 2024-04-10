package com.onedigit.utah.model.integration.kucoin.rest;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
public class KucoinRestTicker {
    String symbol;
    String last;
}
