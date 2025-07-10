package com.yandex.app.logic;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.Duration;

public class DurationTypeAdapter implements JsonSerializer<Duration>, JsonDeserializer<Duration> {

    @Override
    public JsonElement serialize(Duration duration, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(duration.toMinutes());
    }

    @Override
    public Duration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        long minutes = json.getAsLong();
        return Duration.ofMinutes(minutes);
    }
}
