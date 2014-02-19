package com.plnyyanks.frcvolhelper.tba;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.plnyyanks.frcvolhelper.activities.StartActivity;
import com.plnyyanks.frcvolhelper.database.DatabaseHandler;
import com.plnyyanks.frcvolhelper.datatypes.Event;
import com.plnyyanks.frcvolhelper.json.JSONManager;

import org.json.JSONObject;

/**
 * Created by phil on 2/19/14.
 */
public class TBA_EventDetailFetcher extends AsyncTask<String,String,String> {

    private static Activity activity;
    private static String event;

    public TBA_EventDetailFetcher(Activity parentActivity,String eventKey){
        activity = parentActivity;
        event = eventKey;
    }

    @Override
    protected String doInBackground(String... strings) {
        return GET_Request.getWebData("http://www.thebluealliance.com/api/v1/event/details?event="+event);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        loadIntoDatabase(s);

    }

    private void loadIntoDatabase(String data){
        Event event = new Event();
        JsonObject eventObject = JSONManager.getAsJsonObject(data);

        String key = eventObject.get("key").toString();
        event.setEventKey(key.substring(1, key.length()-1));

        String name = eventObject.get("name").toString();
        event.setEventName(name.substring(1, name.length()-1));

        String year = eventObject.get("year").toString();
        event.setEventYear(Integer.parseInt(year.substring(1, year.length()-1)));

        String location = eventObject.get("location").toString();
        event.setEventLocation(location.substring(1, location.length()-1));

        String start = eventObject.get("start_date").toString();
        event.setEventStart(start.substring(1, start.length()-1));

        String end = eventObject.get("end_date").toString();
        event.setEventEnd(end.substring(1, end.length()-1));

        if(StartActivity.db.addEvent(event) == -1){
            Toast.makeText(activity, "Error writing to database",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(activity, "Info downloaded for " + this.event, Toast.LENGTH_SHORT).show();
        }
    }
}
