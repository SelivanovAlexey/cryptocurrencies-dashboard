package com.onedigit.utah.model2;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Builder
public class CoinDTO {
    private String ticker;
    private Exchange exchange;
    private BigDecimal price;
//    private List<SpreadDTO> spreads;
//    private Map<Exchange, List<NetworkAvailabilityDTO>> networkAvailabilityToExchange = new HashMap<>();

//    public CoinDTO(@NonNull String ticker, List<SpreadDTO> spreads) {
//        this.ticker = ticker;
//        this.spreads = spreads;
//    }


}
