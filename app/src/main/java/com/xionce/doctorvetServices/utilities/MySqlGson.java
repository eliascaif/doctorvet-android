package com.xionce.doctorvetServices.utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MySqlGson {
    private static Gson getGson = null;
    private static Gson postGson = null;

    public static Gson getGson() {
        if (getGson == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Boolean.class, new GsonBooleanTypeAdapter());
            gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss");
            getGson = gsonBuilder.create();
        }

        return getGson;
    }
    public static JsonElement getDataFromResponse(String response) {
        JsonElement data = MySqlGson.getGson().fromJson(response, JsonObject.class).get("data");
        return data;
    }
    public static String getStatusFromResponse(String response) {
        String status = MySqlGson.getGson().fromJson(response, JsonObject.class).get("status").getAsString();
        return status;
    }

    public static Gson postGson() {
        if (postGson == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Boolean.class, new GsonBooleanTypeAdapter());
            gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss");
            gsonBuilder.excludeFieldsWithoutExposeAnnotation();
            postGson = gsonBuilder.create();
        }

        return postGson;
    }
    public static String getPostJsonString(Object object) {
        return postGson().toJson(object);
    }

}
