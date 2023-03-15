package com.onedigit.utah.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

@JsonInclude(JsonInclude.Include. NON_NULL)
public class JsonSerializable {

    @SneakyThrows
    public String asJsonString(ObjectMapper mapper){
        return mapper.writeValueAsString(this);
    }
}
