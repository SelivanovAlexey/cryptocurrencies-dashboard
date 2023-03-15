package com.onedigit.utah.model.api.kucoin.rest;

import lombok.Value;

@Value
public class KucoinRestInstanceServer {
    String endpoint;
    Boolean encrypt;
    String protocol;
    Integer pingInterval;
    Integer pingTimeout;
}
