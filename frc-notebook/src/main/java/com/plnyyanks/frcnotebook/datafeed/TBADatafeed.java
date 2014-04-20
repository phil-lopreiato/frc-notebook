package com.plnyyanks.frcnotebook.datafeed;

import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.plnyyanks.frcnotebook.Constants;
import com.plnyyanks.frcnotebook.activities.StartActivity;
import com.plnyyanks.frcnotebook.database.PreferenceHandler;
import com.plnyyanks.frcnotebook.datatypes.Event;
import com.plnyyanks.frcnotebook.datatypes.ListElement;
import com.plnyyanks.frcnotebook.datatypes.ListHeader;
import com.plnyyanks.frcnotebook.datatypes.ListItem;
import com.plnyyanks.frcnotebook.datatypes.Match;
import com.plnyyanks.frcnotebook.datatypes.Team;
import com.plnyyanks.frcnotebook.json.JSONManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * Created by phil on 4/2/14.
 */
public class TBADatafeed {

    public static String fetchEventDetails_TBAv1(String eventKey) {
        String data = GET_Request.getWebData("http://www.thebluealliance.com/api/v1/event/details?event=" + eventKey, true);

        JsonObject eventObject = JSONManager.getAsJsonObject(data);
        JsonArray teams = eventObject.getAsJsonArray("teams");
        JsonArray matches = eventObject.getAsJsonArray("matches");

        Event event = new Event();

        event.setEventKey(eventKey);

        String name = eventObject.get("name").getAsString();
        event.setEventName(name);

        String shortName = eventObject.get("short_name").getAsString();
        event.setShortName(shortName);

        String year = eventObject.get("year").getAsString();
        event.setEventYear(Integer.parseInt(year));

        String location = eventObject.get("location").getAsString();
        event.setEventLocation(location);

        String start = eventObject.get("start_date").getAsString();
        event.setEventStart(start);

        String end = eventObject.get("end_date").getAsString();
        event.setEventEnd(end);

        Log.d(Constants.LOG_TAG, "official: " + eventObject.get("official"));
        event.setOfficial(eventObject.get("official").getAsBoolean());

        long eventResult = StartActivity.db.addEvent(event);

        if (eventResult == -1) {
            return "Error writing event to database";
        }

        Iterator<JsonElement> iterator = teams.iterator();
        JsonElement teamElement;
        Team team;

        long teamResult = 0;
        while (iterator.hasNext() && teamResult != -1) {
            teamElement = iterator.next();
            team = new Team();
            team.setTeamKey(teamElement.getAsString());
            team.setTeamNumber(Integer.parseInt(team.getTeamKey().substring(3)));
            team.addEvent(eventKey);

            teamResult = StartActivity.db.addTeam(team);
        }

        if (teamResult == -1) {
            return "Error writing teams to database";
        } else {
            if(matches.size()>0){
                TBADatafeed.fetchMatches_TBAv1(matches.toString());
            }

            return "Info downloaded for " + eventKey;
        }

    }

    public static String fetchEventDetails_TBAv2(String eventKey){
        String eventData = GET_Request.getWebData("http://www.thebluealliance.com/api/v2/event/" + eventKey, true);
        String teamData = GET_Request.getWebData("http://www.thebluealliance.com/api/v2/event/" + eventKey+"/teams",true);

        JsonObject eventObject = JSONManager.getAsJsonObject(eventData);
        JsonArray teams = JSONManager.getasJsonArray(teamData);

        Event event = new Event();
        event.setEventKey(eventKey);
        event.setEventName(eventObject.get("name").getAsString());
        event.setShortName(eventObject.get("short_name").getAsString());
        event.setEventYear(eventObject.get("year").getAsInt());
        event.setEventLocation(eventObject.get("location").getAsString());
        event.setEventStart(eventObject.get("start_date").getAsString());
        event.setEventEnd(eventObject.get("end_date").getAsString());
        event.setOfficial(eventObject.get("official").getAsBoolean());

        long eventResult = StartActivity.db.addEvent(event);

        if (eventResult == -1) {
            return "Error writing event to database";
        }

        Iterator<JsonElement> iterator = teams.iterator();
        JsonObject teamObject;
        Team team;

        long teamResult = 0;
        while (iterator.hasNext() && teamResult != -1) {
            teamObject = iterator.next().getAsJsonObject();
            team = new Team();
            team.setTeamKey(teamObject.get("key").getAsString());
            team.setTeamNumber(teamObject.get("team_number").getAsInt());
            team.addEvent(eventKey);

            teamResult = StartActivity.db.addTeam(team);
        }

        if (teamResult == -1) {
            return "Error writing teams to database";
        } else {
            if(PreferenceHandler.getDataSource() == Constants.DATAFEED_SOURCES.TBAv2)
                fetchMatches_TBAv2(eventKey);
            else if(PreferenceHandler.getDataSource() == Constants.DATAFEED_SOURCES.USFIRST)
                USFIRSTParser.fetchMatches_USFIRST(eventKey);
            return "Info downloaded for " + eventKey;
        }

    }

    public static LinkedHashMap<String, ListItem> fetchEvents_TBAv1(String year) {
        String data = GET_Request.getWebData("http://www.thebluealliance.com/api/v1/events/list?year=" + year, true);
        JsonArray result = JSONManager.getasJsonArray(data);

        //now, add the events to the event picker activity
        Iterator<JsonElement> iterator = result.iterator();

        JsonObject element;
        String eventName, eventKey;
        ArrayList<Event> list = new ArrayList<Event>();
        for (int i = 0; i < result.size() && iterator.hasNext(); i++) {
            element = iterator.next().getAsJsonObject();
            eventName = element.get("name").getAsString();
            eventKey = element.get("key").getAsString();

            Event e = new Event();
            e.setEventKey(eventKey);
            e.setEventName(eventName);
            e.setEventStart(element.get("start_date").getAsString());
            list.add(e);
        }

        Collections.sort(list);
        int eventWeek = Integer.parseInt(Event.weekFormatter.format(new Date())),
                currentWeek;
        LinkedHashMap<String, ListItem> output = new LinkedHashMap<String, ListItem>();
        for (Event e : list) {
            currentWeek = e.getCompetitionWeek();
            if (eventWeek != currentWeek) {
                String header;
                if (currentWeek == 9) {
                    header = year + " Championship Event";
                } else {
                    header = year + " Week " + currentWeek;
                }
                output.put("week" + currentWeek, new ListHeader(header));
            }
            eventWeek = currentWeek;

            output.put(e.getEventKey(), new ListElement(e.getEventName(), e.getEventKey()));
        }

        return output;
    }

    /* Can't do this without lots of API requests because v2 of the API only gives an array of event keys.
          So we'll stick with v1 for this part now
     */
    public static HashMap<String, ListItem> fetchEvents_TBAv2(String year) {
        /*String data = GET_Request.getWebData("http://www.thebluealliance.com/api/v2/events/" + year, true);
        JsonArray result = JSONManager.getasJsonArray(data);

        //now, add the events to the event picker activity
        Iterator<JsonElement> iterator = result.iterator();

        JsonObject element;
        String eventName, eventKey;
        ArrayList<Event> list = new ArrayList<Event>();
        for (int i = 0; i < result.size() && iterator.hasNext(); i++) {
            Event e = new Event();
            e.setEventKey(eventKey);
            e.setEventName(eventName);
            e.setEventStart(element.get("start_date").getAsString());
            list.add(e);
        }

        Collections.sort(list);
        int eventWeek = Integer.parseInt(Event.weekFormatter.format(new Date())),
                currentWeek;*/
        HashMap<String, ListItem> output = new HashMap<String, ListItem>();
        /*for (Event e : list) {
            currentWeek = e.getCompetitionWeek();
            if (eventWeek != currentWeek) {
                String header;
                if (currentWeek == 9) {
                    header = year + " Championship Event";
                } else {
                    header = year + " Week " + currentWeek;
                }
                output.put("week" + currentWeek,new ListHeader(header));
            }
            eventWeek = currentWeek;

            output.put(e.getEventKey(),new ListElement(e.getEventName(), e.getEventKey()))
        }
        */
        return output;
    }

    public static void fetchMatches_TBAv1(String matchesToFetch) {
        String matchList = matchesToFetch.replaceAll("\"", "");
        matchList = matchList.substring(1, matchList.length() - 1);

        String requestString = "http://www.thebluealliance.com/api/v1/match/details?match=" + matchList;
        String result = GET_Request.getWebData(requestString, true);

        JsonArray matches = JSONManager.getasJsonArray(result);
        Iterator<JsonElement> iterator = matches.iterator();
        Match match;
        JsonObject element, alliances, red, blue;
        JsonArray redTeams, blueTeams;
        while (iterator.hasNext()) {
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

    public static void fetchMatches_TBAv2(String eventKey) {
        String requestString = "http://www.thebluealliance.com/api/v2/event/" + eventKey + "/matches";
        String result = GET_Request.getWebData(requestString, true);

        JsonArray matches = JSONManager.getasJsonArray(result);
        Iterator<JsonElement> iterator = matches.iterator();
        Match match;
        JsonObject element, alliances, red, blue;
        JsonArray redTeams, blueTeams;
        while (iterator.hasNext()) {
            //for each match we get data for...
            element = iterator.next().getAsJsonObject();
            match = new Match();
            match.setMatchKey(element.get("key").getAsString());
            match.setMatchType(Constants.MATCH_LEVELS.get(element.get("comp_level").getAsString()));
            match.setMatchNumber(element.get("match_number").getAsInt());
            match.setSetNumber(element.get("set_number").getAsInt());

            //now, for alliances. Hardest part of JSON wizardry...
            alliances = element.get("alliances").getAsJsonObject();
            red = alliances.get("red").getAsJsonObject();
            redTeams = red.get("teams").getAsJsonArray();
            blue = alliances.get("blue").getAsJsonObject();
            blueTeams = blue.get("teams").getAsJsonArray();

            match.setRedScore(red.get("score").getAsInt());
            match.setBlueScore(blue.get("score").getAsInt());

            match.setRedAlliance(redTeams.toString());
            match.setBlueAlliance(blueTeams.toString());
            StartActivity.db.addMatch(match);
        }
    }
}
