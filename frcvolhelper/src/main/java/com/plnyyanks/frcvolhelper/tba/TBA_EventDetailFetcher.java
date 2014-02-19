package com.plnyyanks.frcvolhelper.tba;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.plnyyanks.frcvolhelper.Constants;
import com.plnyyanks.frcvolhelper.activities.StartActivity;
import com.plnyyanks.frcvolhelper.database.DatabaseHandler;
import com.plnyyanks.frcvolhelper.datatypes.Event;
import com.plnyyanks.frcvolhelper.datatypes.Team;
import com.plnyyanks.frcvolhelper.json.JSONManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

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
        JsonObject eventObject = JSONManager.getAsJsonObject(data);
        JsonArray teams = eventObject.getAsJsonArray("teams");

        String eventKey = eventObject.get("key").toString();

        if(addEventDetails(eventObject,eventKey) == -1 ){
            Toast.makeText(activity, "Error writing event to database",Toast.LENGTH_SHORT).show();
        }else{
            if(addAttendingTeams(teams,eventKey) == -1){
                Toast.makeText(activity, "Error writing teams to database",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(activity, "Info downloaded for " + this.event, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private long addEventDetails(JsonObject data, String eventKey){
        Event event = new Event();

        String key = eventKey;

        String name = data.get("name").toString();
        event.setEventName(name.substring(1, name.length()-1));

        String year = data.get("year").toString();
        event.setEventYear(Integer.parseInt(year.substring(1, year.length()-1)));

        String location = data.get("location").toString();
        event.setEventLocation(location.substring(1, location.length()-1));

        String start = data.get("start_date").toString();
        event.setEventStart(start.substring(1, start.length()-1));

        String end = data.get("end_date").toString();
        event.setEventEnd(end.substring(1, end.length()-1));

        return StartActivity.db.addEvent(event);
    }

    private long addAttendingTeams(JsonArray data, String eventKey){
        Iterator<JsonElement> iterator = data.iterator();
        JsonElement teamElement;
        Team team;

        long result = 0;
        while(iterator.hasNext() && result != -1){
            teamElement = iterator.next();
            team = new Team();
            team.setTeamKey(teamElement.getAsString());
            team.setTeamNumber(Integer.parseInt(team.getTeamKey().substring(3)));
            team.addEvent(eventKey);

            result = StartActivity.db.addTeam(team);
        }

        return result;
    }
}
