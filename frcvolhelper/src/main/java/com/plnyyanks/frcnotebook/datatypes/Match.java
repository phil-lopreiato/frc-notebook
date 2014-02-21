package com.plnyyanks.frcnotebook.datatypes;

import android.annotation.TargetApi;
import android.os.Build;

import com.google.gson.JsonArray;
import com.plnyyanks.frcnotebook.activities.StartActivity;
import com.plnyyanks.frcnotebook.json.JSONManager;

/**
 * Created by phil on 2/19/14.
 */
public class Match implements Comparable<Match>{
    private String  matchKey,
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
        this.blueAlliance = blueAlliance;
        this.redAlliance = redAlliance;
        this.blueScore = blueScore;
        this.redScore = redScore;
    }

    public String getMatchKey() {
        return matchKey;
    }

    public void setMatchKey(String matchKey) {
        this.matchKey = matchKey;
    }

    public String getMatchType() {
        return matchType;
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

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public int compareTo(Match match) {
            if(this.setNumber == match.getSetNumber()){
                return Integer.compare(this.matchNumber,match.getMatchNumber());
            }else{
                return Integer.compare(this.setNumber,match.getSetNumber());
            }
    }
}