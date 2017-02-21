package com.geomslayer.utils;

import com.geomslayer.models.Rate;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

public class RatesDeserializer implements JsonDeserializer<Rate> {

    public Rate deserialize(JsonElement json, Type typeOfT,
                            JsonDeserializationContext context) throws JsonParseException {
        Rate rate = null;
        if (json.isJsonObject()) {
            Set<Map.Entry<String, JsonElement>> entries =
                    json.getAsJsonObject().entrySet();
            if (entries.size() > 0) {
                Map.Entry<String, JsonElement> entry = entries.iterator().next();
                rate = new Rate(entry.getKey(), entry.getValue().getAsDouble());
            }
        }
        return rate;
    }

}
