package com.plnyyanks.frcvolhelper.json;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import com.plnyyanks.frcvolhelper.Constants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    public static ArrayList<String> getAsStringArrayList(String input){
        if(parser == null)
            parser = new JsonParser();
        Iterator<JsonElement> iterator = parser.parse(input).getAsJsonArray().iterator();
        ArrayList<String> output = new ArrayList<String>();
        JsonElement element;
        while(iterator.hasNext()){
            element = iterator.next();
            output.add(element.getAsString());
        }
        return output;
    }

    public static ArrayList<JsonObject> getAsObjectArrayList(String input){
        if(parser == null)
            parser = new JsonParser();
        Iterator<JsonElement> iterator = parser.parse(input).getAsJsonArray().iterator();
        ArrayList<JsonObject> output = new ArrayList<JsonObject>();
        JsonElement element;
        while(iterator.hasNext()){
            element = iterator.next();
            output.add(element.getAsJsonObject());
        }
        return output;
    }
    public static String flattenToJsonArray(ArrayList<String> input){
        if(input == null ||input.size()==0)
            return "[]";
        JsonArray array = new JsonArray();
        for(String i:input){
            array.add(new JsonPrimitive(i));
        }
        return array.toString();
    }
}
