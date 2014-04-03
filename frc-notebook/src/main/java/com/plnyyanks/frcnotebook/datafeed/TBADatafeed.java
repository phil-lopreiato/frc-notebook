package com.plnyyanks.frcnotebook.datafeed;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.plnyyanks.frcnotebook.Constants;
import com.plnyyanks.frcnotebook.activities.StartActivity;
import com.plnyyanks.frcnotebook.datatypes.Match;
import com.plnyyanks.frcnotebook.json.JSONManager;

import java.util.Iterator;

/**
 * Created by phil on 4/2/14.
 */
public class TBADatafeed {

    public static void fetchMatches_TBAv1(String matchesToFetch){
        String matchList = matchesToFetch.replaceAll("\"", "");
        matchList = matchList.substring(1, matchList.length()-1);

        String requestString = "http://www.thebluealliance.com/api/v1/match/details?match="+matchList;
        String result = GET_Request.getWebData(requestString,true);

        JsonArray matches = JSONManager.getasJsonArray(result);
        Iterator<JsonElement> iterator = matches.iterator();
        Match match;
        JsonObject element,alliances,red,blue;
        JsonArray redTeams,blueTeams;
        while(iterator.hasNext()){
            //for each match we get data for...
            element = iterator.next().getAsJsonObject();
            match = new Match();
            match.setMatchKey(element.get("key").getAsString());
            match.setMatchType(Constants.MATCH_LEVELS.get(element.get("competition_level").getAsString()));
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
    }
}
