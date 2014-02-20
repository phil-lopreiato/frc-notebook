package com.plnyyanks.frcvolhelper.tba;

import android.os.AsyncTask;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.plnyyanks.frcvolhelper.activities.StartActivity;
import com.plnyyanks.frcvolhelper.datatypes.Match;
import com.plnyyanks.frcvolhelper.json.JSONManager;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by phil on 2/20/14.
 */
public class TBA_MatchDetailFetcher extends AsyncTask<String,String,String>{

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

            match.setRedAlliance(new int[]{Integer.parseInt(redTeams.get(0).getAsString().substring(3)),Integer.parseInt(redTeams.get(1).getAsString().substring(3)),Integer.parseInt(redTeams.get(2).getAsString().substring(3))});
            match.setBlueAlliance(new int[]{Integer.parseInt(blueTeams.get(0).getAsString().substring(3)), Integer.parseInt(blueTeams.get(1).getAsString().substring(3)), Integer.parseInt(blueTeams.get(2).getAsString().substring(3))});

            StartActivity.db.addMatch(match);

        }
        return "";
    }
}
