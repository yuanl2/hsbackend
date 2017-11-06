package com.wxpay;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by yuanl2 on 2017/11/06.
 */
public class JsonUtil {
    private static org.slf4j.Logger log = LoggerFactory.getLogger(JsonUtil.class);

    public static ObjectNode warpJsonNodeResponse(JsonNode obj){
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode=objectMapper.createObjectNode();
        objectNode.put("code", 1);
        objectNode.put("response", obj);
        return objectNode;
    }

    public static JsonNode objectToJsonNode(Object obj){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String objJson=objectMapper.writeValueAsString(obj);
            JsonNode jsonNode = objectMapper.readTree(objJson);
            return jsonNode;
        } catch (JsonProcessingException e) {
            log.error("objectToJsonNode error " + e);
        } catch (IOException e) {
            log.error("objectToJsonNode error " + e);
        }
        return null;
    }
}