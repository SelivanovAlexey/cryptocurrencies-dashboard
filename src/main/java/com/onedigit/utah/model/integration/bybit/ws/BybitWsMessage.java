package com.onedigit.utah.model.integration.bybit.ws;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@Builder
@Jacksonized
public class BybitWsMessage {
    String req_id;
    String op;
    List<String> args;
    String ret_msg;
    Boolean success;
}
