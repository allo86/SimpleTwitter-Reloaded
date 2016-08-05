package com.codepath.apps.allotweets.network.deserializer;

import com.codepath.apps.allotweets.model.Size;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by ALLO on 3/8/16.
 */
public class SizeDeserializer implements JsonDeserializer<Size> {

    @Override
    public Size deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Gson gson = new Gson();
        if (json.getAsJsonObject().has("medium")) {
            return gson.fromJson(json.getAsJsonObject().get("medium").getAsJsonObject(), Size.class);
        } else if (json.getAsJsonObject().has("large")) {
            return gson.fromJson(json.getAsJsonObject().get("large").getAsJsonObject(), Size.class);
        } else if (json.getAsJsonObject().has("small")) {
            return gson.fromJson(json.getAsJsonObject().get("small").getAsJsonObject(), Size.class);
        }
        return null;
    }

}
