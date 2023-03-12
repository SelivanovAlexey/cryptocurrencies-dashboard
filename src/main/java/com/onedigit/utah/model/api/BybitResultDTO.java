package com.onedigit.utah.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.List;

@Value
public class BybitResultDTO {
    @JsonProperty("list")
    List<BybitTickerDTO> tickers;
    String category;
}
