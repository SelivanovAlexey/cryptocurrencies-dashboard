package com.onedigit.utah.model;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@RequiredArgsConstructor
public class CoinDTO {
    @NonNull
    private String ticker;
    @NonNull
    private Map<Exchange, BigDecimal> priceToExchange;
    private List<SpreadDTO> spreads;
}
