package com.onedigit.utah.model.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.onedigit.utah.model.Exchange;
import com.onedigit.utah.model.SpreadDTO;
import com.onedigit.utah.util.JsonSerializable;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include. NON_NULL)
public class CoinUpdateDTO extends JsonSerializable {
    private String ticker;
    private Exchange exchange;
    private BigDecimal price;
    private List<SpreadDTO> spreads;

}
