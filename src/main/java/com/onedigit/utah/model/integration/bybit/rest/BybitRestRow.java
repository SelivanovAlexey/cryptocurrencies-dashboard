package com.onedigit.utah.model.integration.bybit.rest;

import lombok.Value;

import java.util.List;

@Value
public class BybitRestRow{
    String name;
    String coin;
    String remainAmount;
    List<BybitRestChain> chains;
}
