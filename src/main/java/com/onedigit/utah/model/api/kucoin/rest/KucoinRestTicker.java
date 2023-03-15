package com.onedigit.utah.model.api.kucoin.rest;

import com.onedigit.utah.util.JsonSerializable;
import lombok.EqualsAndHashCode;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@Value
public class KucoinRestTicker extends JsonSerializable {
    String symbol;
    String last;
}
