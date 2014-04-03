package com.plnyyanks.frcnotebook.datatypes;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import com.google.gson.JsonArray;
import com.plnyyanks.frcnotebook.activities.StartActivity;
import com.plnyyanks.frcnotebook.json.JSONManager;

import java.util.HashMap;

/**
 * Created by phil on 2/19/14.
 */
public class Match implements Comparable<Match>{

    public enum MATCH_TYPES{
        QUAL,QUARTER,SEMI,FINAL;
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
        if(isOfType(MATCH_TYPES.QUAL)){
            //return buildMatchKey(matchKey.split("_")[0],QUAL_)
        }
        return "";
    }

    public void setMatchKey(String matchKey) {
        this.matchKey = matchKey;
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
            return matchType+" "+matchNumber;
        }else{
            return matchType+" "+setNumber+" Match "+matchNumber;
        }
    }

    public String getTitle(boolean showEvent){
        if(showEvent){
            return getParentEvent().getShortName()+" "+getTitle();
        }else{
            return getTitle();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public int compareTo(Match match) {
            if(this.setNumber == match.getSetNumber()){
                return Integer.compare(this.matchNumber,match.getMatchNumber());
            }else{
                return Integer.compare(this.setNumber,match.getSetNumber());
            }
    }

    public static String buildMatchKey(String eventKey,MATCH_TYPES type,int set, int match){
        return eventKey+"_"+type+(type==MATCH_TYPES.QUAL?"":set)+"m"+match;
    }
}