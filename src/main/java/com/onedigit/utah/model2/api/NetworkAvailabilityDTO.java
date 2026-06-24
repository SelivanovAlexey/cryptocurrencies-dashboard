package com.onedigit.utah.model2.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.onedigit.utah.model.Exchange;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NetworkAvailabilityDTO {
    @JsonProperty("tt")
    String ticker;
    @JsonProperty("e")
    Exchange exchange;
    @JsonProperty("cn")
    String networkChainName;
    @JsonProperty("ct")
    String networkChainType;
    @JsonProperty("d")
    boolean isDepositAvailable;
    @JsonProperty("w")
    boolean isWithdrawAvailable;
    @JsonProperty("f")
    BigDecimal minWithdrawalFee;
}
