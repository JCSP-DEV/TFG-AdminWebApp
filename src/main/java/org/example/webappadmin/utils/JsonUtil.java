package org.example.webappadmin.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

public class JsonUtil {
    private static final Logger LOGGER = Logger.getLogger(JsonUtil.class.getName());
    
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .setLenient()
            .create();

    // Convert object to JSON
    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    // Convert JSON to object of specified class
    public static <T> T fromJson(String json, Class<T> classOfT) throws JsonSyntaxException {
        return gson.fromJson(json, classOfT);
    }

    // Convert JSON to list of objects of specified type
    public static <T> List<T> fromJsonToList(String json, Class<T> classOfT) throws JsonSyntaxException {
        Type type = TypeToken.getParameterized(List.class, classOfT).getType();
        return gson.fromJson(json, type);
    }

    // Extract specific field from JSON as String
    public static String getFieldAsString(String json, String fieldName) {
        try {
            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
            
            // Handle nested fields (e.g., "user.email")
            if (fieldName.contains(".")) {
                String[] parts = fieldName.split("\\.");
                JsonElement element = jsonObject;
                
                for (String part : parts) {
                    if (element.isJsonObject()) {
                        element = element.getAsJsonObject().get(part);
                        if (element == null) {
                            return null;
                        }
                    } else {
                        return null;
                    }
                }
                
                if (element.isJsonNull()) {
                    return null;
                }
                return element.getAsString();
            }
            
            // Handle non-nested fields
            if (!jsonObject.has(fieldName)) {
                return null;
            }
            JsonElement element = jsonObject.get(fieldName);
            if (element.isJsonNull()) {
                return null;
            }
            return element.getAsString();
        } catch (Exception e) {
            LOGGER.severe("Error parsing JSON: " + json + " - " + e.getMessage());
            return null;
        }
    }

    // Extract specific field from JSON as generic type
    public static <T> T getFieldAsType(String json, String fieldName, Class<T> type) {
        try {
            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
            if (!jsonObject.has(fieldName)) {
                return null;
            }
            JsonElement element = jsonObject.get(fieldName);
            if (element.isJsonNull()) {
                return null;
            }
            return gson.fromJson(element, type);
        } catch (Exception e) {
            LOGGER.severe("Error parsing JSON: " + json + " - " + e.getMessage());
            return null;
        }
    }
}

