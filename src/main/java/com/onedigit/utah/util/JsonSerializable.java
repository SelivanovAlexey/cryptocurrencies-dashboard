package com.onedigit.utah.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@JsonInclude(JsonInclude.Include. NON_NULL)
@Component
public class JsonSerializable {

    @SneakyThrows
    public String asJsonString(){
        return CommonUtils.getJsonMapper().writeValueAsString(this);
    }
}
