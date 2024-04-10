package com.onedigit.utah.model2.integration.mexc.rest;

import lombok.EqualsAndHashCode;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class MexcRestChain {
    String coin;
    boolean depositEnable;
    boolean withdrawalEnable;
    String network;
    BigDecimal withdrawalFee;
}
