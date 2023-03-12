package com.onedigit.utah.model.api.kucoin;

import lombok.Value;

@Value
public class KucoinInstanceServerDTO {
    String endpoint;
    Boolean encrypt;
    String protocol;
    Integer pingInterval;
    Integer pingTimeout;
}
