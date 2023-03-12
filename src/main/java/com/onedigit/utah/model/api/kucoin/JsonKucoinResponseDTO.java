package com.onedigit.utah.model.api.kucoin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.onedigit.utah.rest.api.JsonResponseDTO;
import lombok.Value;

@Value
public class JsonKucoinResponseDTO extends JsonResponseDTO {
    @JsonProperty("data")
    KucoinDataDTO data;

    String code;
}
