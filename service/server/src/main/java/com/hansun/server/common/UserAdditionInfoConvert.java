package com.hansun.server.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hansun.server.dto.UserAdditionInfo;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import java.io.IOException;

@Convert()
public class UserAdditionInfoConvert implements AttributeConverter<UserAdditionInfo, String> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(UserAdditionInfo userAdditionInfo) {
        try {
            String result = objectMapper.writeValueAsString(userAdditionInfo);
            JSONObject jsonObject = new JSONObject(result);
            return jsonObject.toString();
        } catch (JsonProcessingException ex) {
            logger.error("Unexpected IOEx decoding json from database: " + ex);

            return null;
            // or throw an error
        } catch (JSONException e) {
            logger.error("Unexpected IOEx decoding json from database: " + e);

            return null;        }
    }

    @Override
    public UserAdditionInfo convertToEntityAttribute(String s) {
        try {
            return objectMapper.readValue(s, UserAdditionInfo.class);
        } catch (IOException ex) {
             logger.error("Unexpected IOEx decoding json from database: " + s);
            return null;
        }
    }
}
