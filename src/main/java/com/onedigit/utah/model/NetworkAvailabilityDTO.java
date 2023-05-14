package com.onedigit.utah.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class NetworkAvailabilityDTO {
    String networkChainName;
    String networkChainType;
    boolean isDepositAvailable;
    boolean isWithdrawAvailable;
}
