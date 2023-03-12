package com.onedigit.utah.model.api.kucoin;

import lombok.Value;

@Value
public class KucoinTickerDTO {
    String symbol;
    String last;
}
