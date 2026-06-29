package com.onedigit.utah.model.integration.kucoin.rest;

import lombok.Value;

@Value
public class KucoinRestChain {
    String chainName;
    boolean isWithdrawEnabled;
    boolean isDepositEnabled;
}
