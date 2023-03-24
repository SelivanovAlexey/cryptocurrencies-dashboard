package com.onedigit.utah.model;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@RequiredArgsConstructor
public class CoinDTO {
    @NonNull
    private String ticker;
    @NonNull
    private Exchange exchange;
    @NonNull
    private BigDecimal price;
    private List<SpreadDTO> spreadList;
}
