package com.plnyyanks.frcnotebook.datafeed;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.plnyyanks.frcnotebook.activities.StartActivity;
import com.plnyyanks.frcnotebook.datatypes.Event;
import com.plnyyanks.frcnotebook.datatypes.Team;
import com.plnyyanks.frcnotebook.json.JSONManager;

import java.util.Iterator;

/**
 * Created by phil on 2/19/14.
 */
public class EventDetailFetcher extends AsyncTask<String,String,String> {

    private static Activity activity;
    private static String event;

    public EventDetailFetcher(Activity parentActivity, String eventKey){
        activity = parentActivity;
        event = eventKey;
    }

    @Override
    protected String doInBackground(String... strings) {
        return GET_Request.getWebData("http://www.thebluealliance.com/api/v1/event/details?event=" + event,true);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        loadIntoDatabase(s);

    }

    private void loadIntoDatabase(String data){
        JsonObject eventObject = JSONManager.getAsJsonObject(data);
        JsonArray teams = eventObject.getAsJsonArray("teams");
        JsonArray matches = eventObject.getAsJsonArray("matches");

        String eventKey = eventObject.get("key").getAsString();

        if(addEventDetails(eventObject,eventKey) == -1 ){
            Toast.makeText(activity, "Error writing event to database",Toast.LENGTH_SHORT).show();
        }else{
            if(addAttendingTeams(teams,eventKey) == -1){
                Toast.makeText(activity, "Error writing teams to database",Toast.LENGTH_SHORT).show();
            }else{
                if(addMatches(matches,eventKey) != -1){
                    //success. Will toast within the async task
                }else{
                    Toast.makeText(activity, "Error writing matches to database",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private long addEventDetails(JsonObject data, String eventKey){
        Event event = new Event();

        event.setEventKey(eventKey);

        String name = data.get("name").getAsString();
        event.setEventName(name);

        String shortName = data.get("short_name").getAsString();
        event.setShortName(shortName);

        String year = data.get("year").getAsString();
        event.setEventYear(Integer.parseInt(year));

        String location = data.get("location").getAsString();
        event.setEventLocation(location);

        String start = data.get("start_date").getAsString();
        event.setEventStart(start);

        String end = data.get("end_date").getAsString();
        event.setEventEnd(end);

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

    private long addMatches(JsonArray data, String eventKey){
        if(data.size()>0){
            new MatchDetailFetcher(activity,eventKey).execute(new String[]{data.toString(),eventKey});
        }else{
            Toast.makeText(activity, "Info downloaded for " + this.event, Toast.LENGTH_SHORT).show();
        }
        return 0;
    }
}
