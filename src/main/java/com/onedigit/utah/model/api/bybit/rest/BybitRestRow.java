package com.onedigit.utah.model.api.bybit.rest;

import com.onedigit.utah.util.JsonSerializable;
import lombok.Value;

import java.util.List;

@Value
public class BybitRestRow extends JsonSerializable {
    String name;
    String coin;
    String remainAmount;
    List<BybitRestChain> chains;
}
