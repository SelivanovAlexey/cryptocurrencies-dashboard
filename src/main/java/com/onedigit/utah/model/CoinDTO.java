package com.onedigit.utah.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CoinDTO {
    private String ticker;
    private Double price;
}
