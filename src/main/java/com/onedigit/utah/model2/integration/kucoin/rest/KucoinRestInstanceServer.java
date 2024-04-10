package com.onedigit.utah.model2.integration.kucoin.rest;

import lombok.Value;

@Value
public class KucoinRestInstanceServer {
    String endpoint;
    Boolean encrypt;
    String protocol;
    Integer pingInterval;
    Integer pingTimeout;
}
