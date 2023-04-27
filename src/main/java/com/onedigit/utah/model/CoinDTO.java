package com.onedigit.utah.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include. NON_NULL)
public class CoinDTO {
    private String ticker;
    private Map<Exchange, BigDecimal> priceToExchange;
    private List<SpreadDTO> spreads;

    public CoinDTO(@NonNull String ticker, Map<Exchange, BigDecimal> priceToExchange) {
        this.ticker = ticker;
        this.priceToExchange = priceToExchange;
    }

    public CoinDTO(@NonNull String ticker, List<SpreadDTO> spreads) {
        this.ticker = ticker;
        this.spreads = spreads;
    }
}
