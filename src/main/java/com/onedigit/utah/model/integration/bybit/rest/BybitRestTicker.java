package com.onedigit.utah.model.integration.bybit.rest;

import com.onedigit.utah.util.JsonSerializable;
import lombok.Value;

@Value
public class BybitRestTicker extends JsonSerializable {
    String lastPrice;
    String symbol;
}
