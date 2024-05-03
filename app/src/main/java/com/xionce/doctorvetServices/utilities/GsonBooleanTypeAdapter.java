package com.xionce.doctorvetServices.utilities;

import java.lang.reflect.Type;
import com.google.gson.*;

public class GsonBooleanTypeAdapter implements JsonDeserializer<Boolean>, JsonSerializer<Boolean>
{
    public Boolean deserialize(JsonElement json, Type typeOfT,
                               JsonDeserializationContext context) throws JsonParseException
    {
        int code = json.getAsInt();
        return code == 0 ? false :
                code == 1 ? true :
                        null;
    }

    @Override
    public JsonElement serialize(Boolean src, Type typeOfSrc, JsonSerializationContext context) {
        if (src)
            return new JsonPrimitive(1);

        return new JsonPrimitive(0);
    }
}