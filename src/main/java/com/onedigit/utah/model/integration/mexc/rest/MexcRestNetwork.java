package com.onedigit.utah.model.integration.mexc.rest;

import lombok.Value;

//TODO: equals is impossible due to different storing network names at different exchanges
@Value
public class MexcRestNetwork {
    String coin;
    String network;
    boolean depositEnable;
    boolean withdrawEnable;
}
