package com.onedigit.utah.model2.integration.kucoin.rest;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class KucoinRestChain {
    String chainName;
    String chainId;
    BigDecimal withdrawalMinFee;
    boolean isWithdrawEnabled;
    boolean isDepositEnabled;
}
