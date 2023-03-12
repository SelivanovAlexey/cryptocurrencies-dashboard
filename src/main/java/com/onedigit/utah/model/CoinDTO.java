package com.onedigit.utah.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;
@Data
@AllArgsConstructor
public class CoinDTO {
    private String ticker;
    private Map<Exchange, Double> exchangesInfo;
}
