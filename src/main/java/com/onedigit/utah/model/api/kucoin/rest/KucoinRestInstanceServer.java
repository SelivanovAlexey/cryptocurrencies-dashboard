package com.onedigit.utah.model.api.kucoin.rest;

import com.onedigit.utah.util.JsonSerializable;
import lombok.EqualsAndHashCode;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@Value
public class KucoinRestInstanceServer extends JsonSerializable {
    String endpoint;
    Boolean encrypt;
    String protocol;
    Integer pingInterval;
    Integer pingTimeout;
}
