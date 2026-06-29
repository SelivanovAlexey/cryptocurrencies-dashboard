package com.onedigit.utah.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record NetworkAvailability(
    String networkChainName,
    String networkChainType,
    @JsonProperty("depositAvailable")
    boolean isDepositAvailable,
    @JsonProperty("withdrawAvailable")
    boolean isWithdrawAvailable
){}
