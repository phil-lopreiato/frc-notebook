package com.plnyyanks.frcnotebook.tba;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.plnyyanks.frcnotebook.activities.StartActivity;
import com.plnyyanks.frcnotebook.datatypes.Match;
import com.plnyyanks.frcnotebook.json.JSONManager;

import java.util.Iterator;

/**
 * Created by phil on 2/20/14.
 */
public class TBA_MatchDetailFetcher extends AsyncTask<String,String,String>{

    private Context context;
    private String eventKey;

    public TBA_MatchDetailFetcher(Context inContext,String eventKey){
        this.context = inContext;
        this.eventKey = eventKey;
    }

    @Override
    protected String doInBackground(String... strings) {
        String eventKey = strings[1];
        String matchList = strings[0].replaceAll("\"","");
        matchList = matchList.substring(1, matchList.length()-1);

        String requestString = "http://www.thebluealliance.com/api/v1/match/details?match="+matchList;
        String result = GET_Request.getWebData(requestString);

        JsonArray matches = JSONManager.getasJsonArray(result);
        Iterator<JsonElement> iterator = matches.iterator();
        int[] redAlliance,blueAlliance;
        Match match;
        JsonObject element,alliances,red,blue;
        JsonArray redTeams,blueTeams;
        while(iterator.hasNext()){
            //for each match we get data for...
            element = iterator.next().getAsJsonObject();
            match = new Match();
            match.setMatchKey(element.get("key").getAsString());
            match.setMatchType(element.get("competition_level").getAsString());
            match.setMatchNumber(Integer.parseInt(element.get("match_number").getAsString()));
            match.setSetNumber(Integer.parseInt(element.get("set_number").getAsString()));

            //now, for alliances. Hardest part of JSON wizardry...
            alliances = element.get("alliances").getAsJsonObject();
            red = alliances.get("red").getAsJsonObject();
            redTeams = red.get("teams").getAsJsonArray();
            blue = alliances.get("blue").getAsJsonObject();
            blueTeams = blue.get("teams").getAsJsonArray();

            match.setRedScore(Integer.parseInt(red.get("score").getAsString()));
            match.setBlueScore(Integer.parseInt(blue.get("score").getAsString()));

            match.setRedAlliance(redTeams.toString());
            match.setBlueAlliance(blueTeams.toString());
            StartActivity.db.addMatch(match);

        }
        return "";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        //we've finished. now toast the user
        Toast.makeText(context, "Info downloaded for " + this.eventKey, Toast.LENGTH_SHORT).show();
    }
}
