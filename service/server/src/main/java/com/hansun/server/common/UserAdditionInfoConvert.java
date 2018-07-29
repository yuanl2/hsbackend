package com.hansun.server.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hansun.server.dto.UserAdditionInfo;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import java.io.IOException;

@Convert
public class UserAdditionInfoConvert implements AttributeConverter<UserAdditionInfo, String> {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(UserAdditionInfo userAdditionInfo) {
        try {
            return objectMapper.writeValueAsString(userAdditionInfo);
        } catch (JsonProcessingException ex) {
            return null;
            // or throw an error
        }
    }

    @Override
    public UserAdditionInfo convertToEntityAttribute(String s) {
        try {
            return objectMapper.readValue(s, UserAdditionInfo.class);
        } catch (IOException ex) {
            // logger.error("Unexpected IOEx decoding json from database: " + dbData);
            return null;
        }
    }
}
