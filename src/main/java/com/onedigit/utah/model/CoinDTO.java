package com.onedigit.utah.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CoinDTO {
    private String ticker;
    private Map<Exchange, BigDecimal> priceToExchange = new HashMap<>();
    private List<SpreadDTO> spreads;
    private Map<Exchange, List<NetworkAvailabilityDTO>> networkAvailabilityToExchange = new HashMap<>();

    public CoinDTO(@NonNull String ticker, Map<Exchange, BigDecimal> priceToExchange) {
        this.ticker = ticker;
        this.priceToExchange = priceToExchange;
    }

    public CoinDTO(@NonNull String ticker, List<SpreadDTO> spreads) {
        this.ticker = ticker;
        this.spreads = spreads;
    }
}
