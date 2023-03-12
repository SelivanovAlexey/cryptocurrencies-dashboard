package com.onedigit.utah.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class JsonSerializable {

    @SneakyThrows
    public String asJsonString(ObjectMapper mapper){
        return mapper.writeValueAsString(this);
    }
}
