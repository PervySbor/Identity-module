package identity.module;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import identity.module.exceptions.FailedToReadJsonValueException;
import identity.module.exceptions.ParsingUserRequestException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonManager {
    final ObjectMapper mapper = new ObjectMapper();

    List<String> unwrapPairs(List<String> headers, String jsonString)
            throws ParsingUserRequestException {
        JsonNode node;
        try {
            node = mapper.readTree(jsonString);
        } catch (IOException e){
            ParsingUserRequestException exception = new ParsingUserRequestException("Failed to parse user's request body");
            exception.initCause(e);
            throw exception;
        }
        List<String> result = new ArrayList<>();

        for(String header : headers){
            result.add(node.get(header).asText(""));
        }
        return result;
    }

    String serialize(Object obj)
        throws com.fasterxml.jackson.core.JsonProcessingException{
        return mapper.writeValueAsString(obj);
    }

    public String getStringValue(String json, String property)
            throws JsonProcessingException, FailedToReadJsonValueException {
        String result;
        JsonNode root = mapper.readTree(json);

        JsonNode field = root.at("/" + property);
        if(field.isNull()){
            throw new FailedToReadJsonValueException("no fields with such name encountered in config file");
        }
        result = field.asText();
        if(result.isEmpty()){
            throw new FailedToReadJsonValueException("field " + property + " is not a String");
        }
        return result;
    }
}
