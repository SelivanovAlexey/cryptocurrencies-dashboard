package com.onedigit.utah.model2.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.onedigit.utah.model2.Exchange;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SpreadDTO {
    @JsonProperty("tt")
    private String ticker;
    @JsonProperty("b")
    private Exchange base;
    @JsonProperty("t")
    private Exchange target;
    @JsonProperty("d")
    private Double diff;
}
