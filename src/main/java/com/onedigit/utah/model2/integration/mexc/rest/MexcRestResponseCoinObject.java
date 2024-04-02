package com.onedigit.utah.model2.integration.mexc.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.onedigit.utah.util.JsonSerializable;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Value
public class MexcRestResponseCoinObject extends JsonSerializable {
    String coin;
    @JsonProperty("networkList")
    List<MexcRestChain> chains;
}
