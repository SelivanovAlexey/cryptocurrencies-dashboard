package com.onedigit.utah.model2.integration.mexc.rest;

import com.onedigit.utah.util.JsonSerializable;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Value
public class MexcRestChain extends JsonSerializable {
    String coin;
    boolean depositEnable;
    boolean withdrawalEnable;
    String network;
    BigDecimal withdrawalFee;
}
