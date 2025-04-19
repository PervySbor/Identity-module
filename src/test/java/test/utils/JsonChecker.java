package test.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonChecker {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static boolean fieldExists(String fieldName, String json) throws JsonProcessingException {

        JsonNode node = mapper.readValue(json, JsonNode.class);
        return !node.get(fieldName).isNull();
    }

    public static String get(String fieldName, String json) throws JsonProcessingException {

        JsonNode node = mapper.readValue(json, JsonNode.class);
        return node.get(fieldName).asText();
    }
}
