package identity.module;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JsonManager {
    static final ObjectMapper mapper = new ObjectMapper();

    static List<String> unwrapPairs(List<String> headers, String jsonString)
    throws JsonParseException, IOException {
        JsonNode node = mapper.readTree(jsonString);
        List<String> result = new ArrayList<>();

        for(String header : headers){
            result.add(node.get(header).asText(""));
        }
        return result;
    }
}
