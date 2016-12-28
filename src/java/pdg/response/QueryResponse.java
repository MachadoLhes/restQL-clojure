package pdg.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import pdg.exception.ResponseParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ideais on 14/12/16.
 */
public class QueryResponse {

    private final JsonNode parsed;
    private final ObjectMapper mapper;
    private final String rawString;

    public QueryResponse(String pdgResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.rawString = pdgResponse;
            this.mapper = mapper;
            this.parsed = mapper.readTree(pdgResponse);
        }
        catch(IOException e) {
            throw new ResponseParseException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String field, Class<T> clazz) {
        if (clazz.equals(QueryItemResponse.class)) {
            return (T) new SimpleQueryItemResponse(parsed.get(field));
        }
        else {
            return getClassResponse(field, clazz);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getList(String field, Class<T> clazz) {
        if (clazz.equals(QueryItemResponse.class)) {
            ArrayList<QueryItemResponse> result = new ArrayList<>();
            if (parsed.get(field).isArray()) {
                Iterator<JsonNode> items = parsed.get(field).elements();
                while(items.hasNext()) {
                    result.add(new SimpleQueryItemResponse(items.next()));
                }
            }
            else {
                Iterator<JsonNode> items = parsed.get(field).get("result").elements();
                while(items.hasNext()) {
                    result.add(new ArrayQueryItemResponse(parsed.get(field).get("details"), items.next()));
                }
            }
            return (List<T>) result;
        }
        else {
            return getListClassResponse(field, clazz);
        }
    }

    private <T> List<T> getListClassResponse(String field, Class<T> clazz) {
        List<T> result = new ArrayList<>();

        try {
            if (parsed.get(field).isArray()) {
                Iterator<JsonNode> items = parsed.get(field).elements();
                while(items.hasNext()) {
                    result.add(mapper.treeToValue(items.next().get("result"), clazz));
                }

            } else {
                Iterator<JsonNode> items = parsed.get(field).get("result").elements();
                while (items.hasNext()) {
                    result.add(mapper.treeToValue(items.next(), clazz));
                }

            }
        }
        catch(JsonProcessingException e) {
            throw new ResponseParseException(e);
        }

        return result;
    }

    private <T> T getClassResponse(String field, Class<T> clazz) {
        try {
            JsonNode item = parsed.get(field);
            JsonNode details = item.get("details");
            if (details.get("success").asBoolean()) {
                return mapper.treeToValue(item.get("result"), clazz);
            }
            else {
                throw new ResponseParseException("Field [" + field + "] has failed");
            }
        } catch (JsonProcessingException e) {
            throw new ResponseParseException(e);
        }
    }

    @Override
    public String toString() {
        return rawString;
    }
}