package com.plnyyanks.frcnotebook.datatypes;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import com.google.gson.JsonArray;
import com.plnyyanks.frcnotebook.activities.StartActivity;
import com.plnyyanks.frcnotebook.json.JSONManager;

import java.util.HashMap;

/**
 * File created by phil on 2/19/14.
 * Copyright 2014, Phil Lopreiato
 * This file is part of FRC Notebook.
 * FRC Notebook is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * FRC Notebook is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with FRC Notebook. If not, see http://www.gnu.org/licenses/.
 */
public class Match implements Comparable<Match>{

    public enum SORT_TYPES{ MATCH_NO,NUM_NOTES_ASC,NUM_NOTES_DSC}

    public enum MATCH_TYPES{
        QUAL {
            @Override
            public MATCH_TYPES previous() {
                return null; // see below for options for this line
            };
        },
        QUARTER,
        SEMI,
        FINAL {
            @Override
            public MATCH_TYPES next() {
                return null; // see below for options for this line
            };
        };
        public MATCH_TYPES next() {
            // No bounds checking required here, because the last instance overrides
            return values()[ordinal() + 1];
        }
        public MATCH_TYPES previous() {
            // No bounds checking required here, because the last instance overrides
            return values()[ordinal() - 1];
        }
    }

    public static final HashMap<MATCH_TYPES,String> SHORT_TYPES,LONG_TYPES;
    static{
        SHORT_TYPES = new HashMap<MATCH_TYPES, String>();
        SHORT_TYPES.put(MATCH_TYPES.QUAL,"q");
        SHORT_TYPES.put(MATCH_TYPES.QUARTER,"qf");
        SHORT_TYPES.put(MATCH_TYPES.SEMI,"sf");
        SHORT_TYPES.put(MATCH_TYPES.FINAL,"f");

        LONG_TYPES = new HashMap<MATCH_TYPES, String>();
        LONG_TYPES.put(MATCH_TYPES.QUAL,"Quals");
        LONG_TYPES.put(MATCH_TYPES.QUARTER,"Quarters");
        LONG_TYPES.put(MATCH_TYPES.SEMI,"Semis");
        LONG_TYPES.put(MATCH_TYPES.FINAL,"Finals");
    }

    private String  matchTime,
                    matchKey,
                    matchType,
                    redAlliance,
                    blueAlliance;
    private int     matchNumber;

    private int setNumber;
    private int blueScore;
    private int redScore;

    private static SORT_TYPES sortType = SORT_TYPES.MATCH_NO;


    public Match(){

    }

    public Match(String matchKey, String matchType, int matchNumber, int setNumber, String blueAlliance, String redAlliance, int blueScore, int redScore) {
        this.matchKey = matchKey;
        this.matchType = matchType;
        this.matchNumber = matchNumber;
        this.setNumber = setNumber;
        this.blueAlliance = blueAlliance;
        this.redAlliance = redAlliance;
        this.blueScore = blueScore;
        this.redScore = redScore;
    }

    public String getMatchTime(){
        return matchTime;
    }

    public void setMatchTime(String time){
        matchTime = time;
    }

    public String getMatchKey() {
        return matchKey;
    }

    public String getNextMatch(){
        String eventKey = matchKey.split("_")[0];
        MATCH_TYPES type = getMatchType();
        if(isOfType(MATCH_TYPES.QUAL)){
            String nextQual = buildMatchKey(eventKey,MATCH_TYPES.QUAL,1,matchNumber+1);
            if(StartActivity.db.matchExists(nextQual)){
                return nextQual;
            }else{
                
                return buildMatchKey(eventKey,MATCH_TYPES.QUARTER,1,1);
            }
        }else{
            String nextInSet = buildMatchKey(eventKey,type,setNumber+1,matchNumber);
            if(StartActivity.db.matchExists(nextInSet)){
                return nextInSet;
            }else{
                String nextSet = buildMatchKey(eventKey,type,1,matchNumber+1);
                if(StartActivity.db.matchExists(nextSet)){
                    return nextSet;
                }else if(type.next() !=null){
                    String nextRound = buildMatchKey(eventKey,type.next(),1,1);
                    return nextRound;
                }else{
                    return null;
                }
            }
        }
    }

    public String getPreviousMatch(){
        String eventKey = matchKey.split("_")[0];
        MATCH_TYPES type = getMatchType();
        if(isOfType(MATCH_TYPES.QUAL)){
            String lastQual = buildMatchKey(eventKey,MATCH_TYPES.QUAL,1,matchNumber-1);
            if(StartActivity.db.matchExists(lastQual)){
                return lastQual;
            }else{
                return null;
            }
        }else{
            String lastInSet = buildMatchKey(eventKey,type,setNumber-1,matchNumber);
            if(StartActivity.db.matchExists(lastInSet)){
                return lastInSet;
            }else{
                String lastSet = buildMatchKey(eventKey,type,1,matchNumber-1);
                if(StartActivity.db.matchExists(lastSet)){
                    return lastSet;
                }else if(type.previous()!=null){
                    return buildMatchKey(eventKey,type.previous(),1,1);
                }else{
                    return null;
                }
            }
        }
    }

    public void setMatchKey(String matchKey) {
        this.matchKey = matchKey;
    }

    public String getEventKey(){
        return matchKey.split("_")[0];
    }

    public MATCH_TYPES getMatchType() {
        if(isOfType(MATCH_TYPES.QUAL)) return MATCH_TYPES.QUAL;
        if(isOfType(MATCH_TYPES.QUARTER)) return MATCH_TYPES.QUARTER;
        if(isOfType(MATCH_TYPES.SEMI)) return MATCH_TYPES.SEMI;
        if(isOfType(MATCH_TYPES.FINAL)) return MATCH_TYPES.FINAL;
        return MATCH_TYPES.QUAL;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    public int getMatchNumber() {
        return matchNumber;
    }

    public void setMatchNumber(int matchNumber) {
        this.matchNumber = matchNumber;
    }

    public int getSetNumber() {
        return setNumber;
    }

    public void setSetNumber(int setNumber) {
        this.setNumber = setNumber;
    }

    public String getBlueAlliance() {
        return blueAlliance;
    }

    public void setBlueAlliance(String blueAlliance) {
        this.blueAlliance = blueAlliance;
    }

    public String getRedAlliance() {
        return redAlliance;
    }

    public void setRedAlliance(String redAlliance) {
        this.redAlliance = redAlliance;
    }

    public JsonArray getRedAllianceTeams(){
        return JSONManager.getasJsonArray(redAlliance);
    }

    public JsonArray getBlueAllianceTeams(){
        return JSONManager.getasJsonArray(blueAlliance);
    }

    public int getBlueScore() {
        return blueScore;
    }

    public void setBlueScore(int blueScore) {
        this.blueScore = blueScore;
    }

    public int getRedScore() {
        return redScore;
    }

    public void setRedScore(int redScore) {
        this.redScore = redScore;
    }

    public Event getParentEvent(){
        return StartActivity.db.getEvent(matchKey.split("_")[0]);
    }

    public boolean isOfType(MATCH_TYPES type){
        return matchType.equals(SHORT_TYPES.get(type)) || matchType.equals(LONG_TYPES.get(type));
    }

    public String getTitle(){
        if(getMatchType() == MATCH_TYPES.QUAL){
            return LONG_TYPES.get(getMatchType())+" "+matchNumber;
        }else{
            return LONG_TYPES.get(getMatchType())+" "+setNumber+" Match "+matchNumber;
        }
    }

    public String getTitle(boolean showEvent){
        return getTitle(showEvent,false);
    }

    public String getTitle(boolean showEvent, boolean showNotes){
        String out = "";
        if(showEvent){
            out += getParentEvent().getShortName()+" "+getTitle();
        }else{
            out += getTitle();
        }
        if(showNotes){
            int notes = StartActivity.db.getAllNotes("",getEventKey(),getMatchKey()).size();
            out += (notes>0)?" ("+notes+" Notes)":"";
        }
        return out;
    }

    public static void setSortType(SORT_TYPES type){
        sortType = type;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public int compareTo(Match match) {
        switch(sortType) {
            case MATCH_NO:
            default:
                if (this.setNumber == match.getSetNumber()) {
                    return Integer.compare(this.matchNumber, match.getMatchNumber());
                } else {
                    return Integer.compare(this.setNumber, match.getSetNumber());
                }
            case NUM_NOTES_ASC:
                return Integer.compare(StartActivity.db.getAllNotes("",getEventKey(),getMatchKey()).size(),StartActivity.db.getAllNotes("",match.getEventKey(),match.getMatchKey()).size());
            case NUM_NOTES_DSC:
                return Integer.compare(StartActivity.db.getAllNotes("",match.getEventKey(),match.getMatchKey()).size(),StartActivity.db.getAllNotes("",getEventKey(),getMatchKey()).size());
        }
    }

    public static String buildMatchKey(String eventKey,MATCH_TYPES type,int set, int match){
        return eventKey + "_" + SHORT_TYPES.get(type) + (type == MATCH_TYPES.QUAL ? "" : set) + "m" + match;
    }
}