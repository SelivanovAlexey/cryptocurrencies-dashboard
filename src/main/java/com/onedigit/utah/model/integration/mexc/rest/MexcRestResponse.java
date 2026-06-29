package com.onedigit.utah.model.integration.mexc.rest;

import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

@Value
public class MexcRestResponse {
    String symbol;
    BigDecimal price;

    String coin;
    String name;
    List<MexcRestNetwork> networkList;
}
