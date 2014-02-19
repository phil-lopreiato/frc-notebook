package com.plnyyanks.frcvolhelper.json;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by phil on 2/18/14.
 */
public class JSONManager {
    private static Gson gson;
    private static JsonParser parser;

    public static JsonArray getasJsonArray(String input){
       if(parser == null)
           parser = new JsonParser();

        return parser.parse(input).getAsJsonArray();
    }

    public static JsonObject getAsJsonObject(String input){
        if(parser == null)
            parser = new JsonParser();
        return parser.parse(input).getAsJsonObject();
    }

    public static ArrayList<String> getAsArrayList(String input){
        return gson.fromJson(input, new TypeToken<ArrayList<String>>(){}.getType());
    }

    public static String flattenToJsonArray(ArrayList<String> input){
        return gson.toJson(input,ArrayList.class);
    }
}
