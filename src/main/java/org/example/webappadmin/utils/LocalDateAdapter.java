package org.example.webappadmin.utils;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDate;

/**
 * Custom Gson adapter for serializing and deserializing LocalDate objects.
 *
 * @author Juan Carlos
 */
class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
    /**
     * Serializes a LocalDate object to a JSON string.
     *
     * @param src the LocalDate object to serialize
     * @param typeOfSrc the type of the source object
     * @param context the serialization context
     * @return the serialized JSON element
     */
    @Override
    public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }

    /**
     * Deserializes a JSON string to a LocalDate object.
     *
     * @param json the JSON element to deserialize
     * @param typeOfT the type of the target object
     * @param context the deserialization context
     * @return the deserialized LocalDate object
     * @throws JsonParseException if the JSON string is not a valid date format
     */
    @Override
    public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        return LocalDate.parse(json.getAsString());
    }
}
