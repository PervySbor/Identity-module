package identity.module.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import identity.module.enums.Roles;
import identity.module.exceptions.FailedToReadJsonValueException;
import identity.module.exceptions.ParsingUserRequestException;
import identity.module.models.JwtPayload;
import identity.module.models.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JsonManager {
    static final ObjectMapper mapper = new ObjectMapper();

    public static List<String> unwrapPairs(List<String> headers, String jsonString)
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
        if (result.size() != headers.size()){
            throw new ParsingUserRequestException("Failed to parse user's request body : some values are missing");
        }
        return result;
    }

    public static String getResponseMessage(int statusCode, String error, String message)
            throws JsonProcessingException {
        Response response = new Response(statusCode, error, message);

        return mapper.writeValueAsString(response);
    }

    public static String getJWTPayload(Roles role, UUID session_id)
            throws JsonProcessingException {
        JwtPayload payload = new JwtPayload(role, session_id);
        return mapper.writeValueAsString(payload);
    }

    protected static String serialize(Object obj)
        throws com.fasterxml.jackson.core.JsonProcessingException{
        return mapper.writeValueAsString(obj);
    }

    public static String getStringValue(String json, String property)
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
