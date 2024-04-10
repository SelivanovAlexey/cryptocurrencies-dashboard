package com.onedigit.utah.model.integration.bybit.rest;

import lombok.Value;

@Value
public class BybitRestChain {
    String chainType;
    String confirmation;
    String withdrawFee;
    String depositMin;
    String withdrawMin;
    String chain;
    String chainDeposit;
    String chainWithdraw;
    String minAccuracy;
    String withdrawPercentageFee;
}
