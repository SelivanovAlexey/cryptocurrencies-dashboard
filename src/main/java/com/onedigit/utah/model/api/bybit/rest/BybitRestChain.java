package com.onedigit.utah.model.api.bybit.rest;

import com.onedigit.utah.util.JsonSerializable;
import lombok.Value;

@Value
public class BybitRestChain extends JsonSerializable {
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
