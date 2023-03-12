package com.onedigit.utah.model.api.kucoin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.List;

@Value
public class KucoinDataDTO {
    Long time;
    @JsonProperty("ticker")
    List<KucoinTickerDTO> tickerList;
    String token;
    List<KucoinInstanceServerDTO> instanceServers;

}
