package com.plnyyanks.frcnotebook.datafeed;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.plnyyanks.frcnotebook.Constants;
import com.plnyyanks.frcnotebook.activities.StartActivity;
import com.plnyyanks.frcnotebook.datatypes.Match;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by phil on 4/2/14.
 */
public class USFIRSTParser {
    private static final String URL_PATTERN = "http://www2.usfirst.org/%year%comp/events/%event%/matchresults.html";

    public static void fetchMatches_USFIRST(String year, String eventKey) throws IOException {
        try {
            URL url = new URL(URL_PATTERN.replaceAll("%year%", year).replaceAll("%event%", eventKey));
            parsePage(getPageContents(url),year+eventKey);
        } catch (MalformedURLException e) {
            Log.e(Constants.LOG_TAG,"Malformed URL Excpetion while attempting to fetch match results for "+year+eventKey+"\n"+e.getStackTrace());
        }
    }

    private static Document getPageContents(URL url) throws IOException {
        // TODO check android for Internet connectivity
        Document out = new Document("");
        try {
            out = Jsoup.connect(url.toString()).get();
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG,"IO Exception while fetching match data. \n"+e.toString());
            throw e;
        }
        return out;
    }

    public static void parsePage(Document page,String eventKey) {
        Elements tables = page.select("table");
        if (tables.size() < 4)
            return;
        parseResultTable(tables.get(2),eventKey);
        parseResultTable(tables.get(3),eventKey);
    }

    private static void parseResultTable(Element table,String eventKey) {
        Elements rows = table.select("tr"), cells;
        int len;
        String redScore, blueScore;
        if (rows.size() < 3)
            return;
        Match match;
        for (int r = 2; r < rows.size(); r++) {
            match = new Match();
            cells = rows.get(r).select("td");
            if (cells.size() == 10 || cells.size() == 11) { // qualification table has 10 columns, eliminations has 11
                if (cells.get(0).text().equals("Time")) continue; //FIRST page inconsistency. Grrrr.
                len = cells.size();

                match.setMatchTime(cells.get(0).text());
                match.setRedAlliance ("[\"frc" + cells.get(len - 8).text() + "\",\"frc" + cells.get(len - 7).text() + "\",\"frc" + cells.get(len - 6).text() + "\"]");
                match.setBlueAlliance("[\"frc" + cells.get(len - 5).text() + "\",\"frc" + cells.get(len - 4).text() + "\",\"frc" + cells.get(len - 3).text() + "\"]");

                redScore = cells.get(len - 2).text();
                blueScore = cells.get(len - 1).text();
                match.setRedScore(redScore.equals("") ? -1 : Integer.parseInt(redScore));
                match.setBlueScore(blueScore.equals("") ? -1 : Integer.parseInt(blueScore));

                HashMap<MATCH_PROPS,String> props = parseMatchNumberField(cells.get(1).text());
                match.setMatchType(props.get(MATCH_PROPS.MATCH_TYPE));
                match.setSetNumber(Integer.parseInt(props.get(MATCH_PROPS.MATCH_SET)));
                match.setMatchNumber(Integer.parseInt(props.get(MATCH_PROPS.MATCH_NUM)));

                match.setMatchKey(Match.buildMatchKey(eventKey,match.getMatchType(),match.getSetNumber(),match.getMatchNumber()));

                StartActivity.db.addMatch(match);
            }
        }
    }

    private static HashMap<MATCH_PROPS, String> parseMatchNumberField(String text) {
        HashMap<MATCH_PROPS, String> out = new HashMap<MATCH_PROPS, String>();
        //parse match info based on the given string
        //They look like "34", "Semi 2-2", or "Final 1-1"
        if (text.length() > 3) {
            //there won't be >999 qual matches, so we know this is an elim one
            out.put(MATCH_PROPS.MATCH_TYPE, Constants.MATCH_LEVELS.get(text.substring(0, text.length() - 4)));
            out.put(MATCH_PROPS.MATCH_NUM, text.substring(text.length() - 1));
            out.put(MATCH_PROPS.MATCH_SET, text.substring(text.length() - 3, text.length() - 2));
        } else {
            //this is a qual match
            out.put(MATCH_PROPS.MATCH_TYPE, "q");
            out.put(MATCH_PROPS.MATCH_NUM, text.trim());
            out.put(MATCH_PROPS.MATCH_SET, "1");
        }
        return out;
    }
}
