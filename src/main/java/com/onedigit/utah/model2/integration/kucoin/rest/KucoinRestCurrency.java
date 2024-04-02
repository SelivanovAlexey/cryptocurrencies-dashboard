package com.onedigit.utah.model2.integration.kucoin.rest;

import com.onedigit.utah.util.JsonSerializable;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Value
public class KucoinRestCurrency extends JsonSerializable {
    String currency;
    String name;
    String fullName;
    List<KucoinRestChain> chains;
}
