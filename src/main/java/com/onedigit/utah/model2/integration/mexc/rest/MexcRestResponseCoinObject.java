package com.onedigit.utah.model2.integration.mexc.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.List;

@Value
public class MexcRestResponseCoinObject {
    String coin;
    @JsonProperty("networkList")
    List<MexcRestChain> chains;
}
