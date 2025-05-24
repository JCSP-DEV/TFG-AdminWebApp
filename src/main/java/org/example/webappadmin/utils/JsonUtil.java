package org.example.webappadmin.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

/**
 * Utility class for handling JSON serialization and deserialization operations.
 *
 * @author Juan Carlos
 */
public class JsonUtil {
    private static final Logger LOGGER = Logger.getLogger(JsonUtil.class.getName());
    
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .setLenient()
            .create();

    /**
     * Converts an object to its JSON string representation.
     *
     * @param object the object to convert to JSON
     * @return the JSON string representation of the object
     */
    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    /**
     * Converts a JSON string to an object of the specified class.
     *
     * @param <T> the type of the object to convert to
     * @param json the JSON string to convert
     * @param classOfT the class of the object to convert to
     * @return the converted object
     * @throws JsonSyntaxException if the JSON string is not valid
     */
    public static <T> T fromJson(String json, Class<T> classOfT) throws JsonSyntaxException {
        return gson.fromJson(json, classOfT);
    }

    /**
     * Converts a JSON string to a list of objects of the specified class.
     *
     * @param <T> the type of objects in the list
     * @param json the JSON string to convert
     * @param classOfT the class of the objects in the list
     * @return the list of converted objects
     * @throws JsonSyntaxException if the JSON string is not valid
     */
    public static <T> List<T> fromJsonToList(String json, Class<T> classOfT) throws JsonSyntaxException {
        Type type = TypeToken.getParameterized(List.class, classOfT).getType();
        return gson.fromJson(json, type);
    }

    /**
     * Extracts a string value from a JSON object using a field name.
     * Supports nested fields using dot notation (e.g., "user.address.city").
     *
     * @param json the JSON string to extract from
     * @param fieldName the name of the field to extract
     * @return the string value of the field, or null if not found or invalid
     */
    public static String getFieldAsString(String json, String fieldName) {
        try {
            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
            
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

    /**
     * Extracts a value of a specific type from a JSON object using a field name.
     *
     * @param <T> the type of the value to extract
     * @param json the JSON string to extract from
     * @param fieldName the name of the field to extract
     * @param type the class of the value to extract
     * @return the value of the specified type, or null if not found or invalid
     */
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

