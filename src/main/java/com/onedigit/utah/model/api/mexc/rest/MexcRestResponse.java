package com.onedigit.utah.model.api.mexc.rest;

import com.onedigit.utah.util.JsonSerializable;
import lombok.Value;

@Value
public class MexcRestResponse extends JsonSerializable {
    String symbol;
    String price;
}
