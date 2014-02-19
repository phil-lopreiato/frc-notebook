package com.plnyyanks.frcvolhelper.json;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

/**
 * Created by phil on 2/18/14.
 */
public class JSONManager {
    private static Gson gson;
    private static JsonParser parser;

    public static JsonArray eventStringtoArray(String input){
       if(parser == null)
           parser = new JsonParser();

        return parser.parse(input).getAsJsonArray();
    }
}
