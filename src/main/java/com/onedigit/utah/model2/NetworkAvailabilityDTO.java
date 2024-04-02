package com.onedigit.utah.model2;

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
    String ticker;
    Exchange exchange;
    String networkChainName;
    String networkChainType;
    boolean isDepositAvailable;
    boolean isWithdrawAvailable;
    BigDecimal minWithdrawalFee;
}
