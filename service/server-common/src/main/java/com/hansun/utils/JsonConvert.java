package com.hansun.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonConvert<T> {

    private static ObjectMapper mapper = new ObjectMapper();

    public String objectToJson(Object object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }
    
    public T jsonToObject(String json ,  Class<T> valueType) throws IOException {
        return mapper.readValue(json,valueType);
    }
}
