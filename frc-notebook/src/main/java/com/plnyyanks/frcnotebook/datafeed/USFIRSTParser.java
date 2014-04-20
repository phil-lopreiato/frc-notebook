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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

/**
 * File created by phil on 4/2/14.
 * Copyright 2014, Phil Lopreiato
 * This file is part of FRC Notebook.
 * FRC Notebook is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * FRC Notebook is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with FRC Notebook. If not, see http://www.gnu.org/licenses/.
 */
public class USFIRSTParser {
    private static final String URL_PATTERN = "http://www2.usfirst.org/%year%comp/events/%event%/matchresults.html";

    public static String fetchMatches_USFIRST(String eventKey){
        return fetchMatches_USFIRST(eventKey.substring(0,4),eventKey.substring(4));
    }

    public static String fetchMatches_USFIRST(String year, String eventKey){
        try {
            URL url = new URL(URL_PATTERN.replaceAll("%year%", year).replaceAll("%event%", eventKey));
            parsePage(getPageContents(url),year+eventKey,false);
        } catch (MalformedURLException e) {
            Log.e(Constants.LOG_TAG,"Malformed URL Exception while attempting to fetch match results for "+year+eventKey+"\n"+e.getStackTrace());
            return "Malformed URL Exception while attempting to fetch match results for "+year+eventKey;
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG,"IO Exception while attempting to fetch match results for "+year+eventKey+"\n"+e.getStackTrace());
            return "IO Exception while attempting to fetch match results for "+year+eventKey;
        }
        return "";
    }

    public static String fetchMatchesFromURL(String address,String eventKey){
        try {
            if(!address.startsWith("http://") && !address.startsWith("https://")){
                address = "http://"+address;
            }
            URL url = new URL(address);
            String extension = url.getFile().substring(url.getFile().lastIndexOf(".")+1);
            Log.d(Constants.LOG_TAG,"Extension: "+extension);
            if(extension.equals("html")){
                parsePage(getPageContents(url),eventKey,true);
            }else if(extension.equals("csv")){
                Log.d(Constants.LOG_TAG,"Parsing csv file: "+url);
                return parsePage(getCSVContents(url),eventKey,true);
            }
        } catch (MalformedURLException e) {
            return "Error: Malformed URL";
        } catch (IOException e) {
            return "Error: Unable to fetch matches";
        }
        return "";
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

    public static void parsePage(Document page,String eventKey,boolean addEventToTeams) {
        Elements tables = page.select("table");
        if (tables.size() < 4)
            return;
        parseResultTable(tables.get(2),eventKey,addEventToTeams);
        parseResultTable(tables.get(3),eventKey,addEventToTeams);
    }

    public static String parsePage(List<String[]> lines,String eventKey,boolean addEventToTeams){
        Log.d(Constants.LOG_TAG,"Parsing file: "+lines.toString());
        if(lines.size() < 5){
            Log.w(Constants.LOG_TAG,"File too short. Should be at least 5 lines");
            return "File too short. Should be at least 5 lines";
        }
        //2014 schedule exports start on the 5th line, table should have 16 columns for elims, 15 for quals
        String line[], redScore,blueScore,time;
        int start = 4,len,hour;
        if(lines.get(start)[0].contains("Time")){ //I'm building this to handle both the schedule report and the results report
            start++;                              //irritatingly, the results report has an extra blank line before the actual results happen
        }
        Match match;
        for(int i=start;i<lines.size();i++){
            line = lines.get(i);
            len = line.length;
            match = new Match();

            if(line.length == 15 || line.length == 16){
                //this is an export of the FMS schedule report.
                //<blank>, time, description (elims only), match #, blue 1, surr, blue 2, surr, blue 3, surr, red 1, surr, red 2, surr, red 3, surr

                //skip if blank line (surrogate footer involves column index 2 and is the right width, but the rest is blank
                if(line[1].isEmpty()||line[3].isEmpty()) continue;

                //time formatting is inconsistent. Sometime it's like 03/09 01:30 and others 1:30 PM. I want the latter
                time = line[1];
                if(time.contains("/")){
                    time = time.substring(6);
                    if(!time.contains("AM") && !time.contains("PM")){
                        hour = Integer.parseInt(time.substring(0,time.indexOf(":")));
                        if(hour < 12 && hour >= 7){
                            time += " AM";
                        }else{
                            time += " PM";
                        }
                    }
                }

                match.setMatchTime(time);
                match.setRedAlliance ("[\"frc" + line[len - 6]  + "\",\"frc" + line[len - 4]  + "\",\"frc" + line[len - 2] + "\"]");
                match.setBlueAlliance("[\"frc" + line[len - 12] + "\",\"frc" + line[len - 10] + "\",\"frc" + line[len - 8] + "\"]");

                if(match.getRedAlliance().contains("frc0") || match.getBlueAlliance().contains("frc0")){
                    //no teams scheduled here. skip it.
                    Log.i(Constants.LOG_TAG,"No teams in row "+i+". Skipping...");
                    continue;
                }

                match.setRedScore(-1);
                match.setBlueScore(-1);

                HashMap<MATCH_PROPS,String> props = parseMatchNumberField(line[2]);
                //Log.d(Constants.LOG_TAG,"Match "+props.get(MATCH_PROPS.MATCH_TYPE)+" "+props.get(MATCH_PROPS.MATCH_SET)+"-"+props.get(MATCH_PROPS.MATCH_NUM));
                match.setMatchType(props.get(MATCH_PROPS.MATCH_TYPE));
                match.setSetNumber(Integer.parseInt(props.get(MATCH_PROPS.MATCH_SET)));
                match.setMatchNumber(Integer.parseInt(props.get(MATCH_PROPS.MATCH_NUM)));

                match.setMatchKey(Match.buildMatchKey(eventKey,match.getMatchType(),match.getSetNumber(),match.getMatchNumber()));

                StartActivity.db.addMatch(match);
                if(addEventToTeams) {
                    StartActivity.db.addEventToTeams(new String[]{  "frc"+line[len - 12],
                            "frc"+line[len - 10],
                            "frc"+line[len - 8],
                            "frc"+line[len - 6],
                            "frc"+line[len - 4],
                            "frc"+line[len - 2]}, eventKey);
                }
            }else if(line.length == 10 || line.length == 11){
                //this is an export of the FMS results report
                //time, description (elims only), match #, red 1, red 2, red 3, blue 1, blue 2, blue 3, red score, blue score

                match.setMatchTime(line[0]);
                match.setRedAlliance ("[\"frc" + line[len - 8] + "\",\"frc" + line[len - 7] + "\",\"frc" + line[len - 6] + "\"]");
                match.setBlueAlliance("[\"frc" + line[len - 5] + "\",\"frc" + line[len - 4] + "\",\"frc" + line[len - 3] + "\"]");

                redScore = line[len-2];
                blueScore = line[len-1];
                match.setRedScore(redScore.equals("") ? -1 : Integer.parseInt(redScore));
                match.setBlueScore(blueScore.equals("") ? -1 : Integer.parseInt(blueScore));

                HashMap<MATCH_PROPS,String> props = parseMatchNumberField(line[1]);
                match.setMatchType(props.get(MATCH_PROPS.MATCH_TYPE));
                match.setSetNumber(Integer.parseInt(props.get(MATCH_PROPS.MATCH_SET)));
                match.setMatchNumber(Integer.parseInt(props.get(MATCH_PROPS.MATCH_NUM)));

                match.setMatchKey(Match.buildMatchKey(eventKey,match.getMatchType(),match.getSetNumber(),match.getMatchNumber()));

                StartActivity.db.addMatch(match);
                if(addEventToTeams) {
                    StartActivity.db.addEventToTeams(new String[]{  "frc"+line[len - 8],
                            "frc"+line[len - 7],
                            "frc"+line[len - 6],
                            "frc"+line[len - 5],
                            "frc"+line[len - 4],
                            "frc"+line[len - 3]}, eventKey);
                }
            }else{
                Log.w(Constants.LOG_TAG,"Wrong number of columns: "+line.length);
                return "Wrong number of columns in file";
            }
        }
        return "";
    }

    private static void parseResultTable(Element table,String eventKey,boolean addEventToTeams) {
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
                if(addEventToTeams) {
                    Log.d(Constants.LOG_TAG,"Adding event to teams");
                    StartActivity.db.addEventToTeams(new String[]{  "frc"+cells.get(len - 8).text(),
                            "frc"+cells.get(len - 7).text(),
                            "frc"+cells.get(len - 6).text(),
                            "frc"+cells.get(len - 5).text(),
                            "frc"+cells.get(len - 4).text(),
                            "frc"+cells.get(len - 3).text()}, eventKey);
                }
            }
        }
    }

    private static List<String[]> getCSVContents(URL url) throws IOException{
        Log.d(Constants.LOG_TAG,"Fetching "+url);
        CSVReader reader = new CSVReader(new BufferedReader(new InputStreamReader(url.openStream())));
        return reader.readAll();
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
