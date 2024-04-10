package com.onedigit.utah.model2.integration.bybit.rest;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
public class BybitRestChain {
    String chainType;
    String withdrawFee;
    String chain;
    String chainDeposit;
    String chainWithdraw;
}
