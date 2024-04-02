package com.onedigit.utah.model2.integration.bybit.rest;

import com.onedigit.utah.util.JsonSerializable;
import lombok.EqualsAndHashCode;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@Value
public class BybitRestChain extends JsonSerializable {
    String chainType;
    String withdrawFee;
    String chain;
    String chainDeposit;
    String chainWithdraw;
}
