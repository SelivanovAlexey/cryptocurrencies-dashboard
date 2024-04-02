package com.onedigit.utah.model2.integration.kucoin.rest;

import com.onedigit.utah.util.JsonSerializable;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Value
public class KucoinRestChain extends JsonSerializable {
    String chainName;
    String chainId;
    BigDecimal withdrawalMinFee;
    boolean isWithdrawEnabled;
    boolean isDepositEnabled;
}
