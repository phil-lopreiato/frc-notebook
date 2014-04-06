package com.plnyyanks.frcnotebook.datatypes;

import android.annotation.TargetApi;
import android.os.Build;

import com.plnyyanks.frcnotebook.activities.StartActivity;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by phil on 2/19/14.
 */
public class Team implements Comparable<Team>{

    public static final int COMPARE_TEAM_NUMBER = 0,COMPARE_NUM_NOTES = 1;
    private static int compareType = COMPARE_TEAM_NUMBER;

    private String              teamKey;
    private String              teamName;
    private String              teamWebsite;
    private int                 teamNumber;
    private ArrayList<String>   teamEvents = new ArrayList<String>();

    public Team(){
        teamEvents = new ArrayList<String>();
    }

    public Team(String teamKey, int teamNumber) {
        this.teamKey = teamKey;
        this.teamNumber = teamNumber;
        teamEvents = new ArrayList<String>();
    }

    public Team(String teamKey, int teamNumber, String teamName, String website, ArrayList<String> teamEvents) {
        this.teamKey = teamKey;
        this.teamNumber = teamNumber;
        this.teamName = teamName;
        this.teamWebsite = website;
        this.teamEvents = teamEvents;
    }

    public Team(String teamKey, int teamNumber, String teamName, String website, String event) {
        this.teamKey = teamKey;
        this.teamNumber = teamNumber;
        this.teamName = teamName;
        this.teamWebsite = website;
        addEvent(event);
    }

    public void addEvent(String eventKey){
        if(teamEvents == null)
            teamEvents = new ArrayList<String>();

        teamEvents.add(eventKey);
    }

    public String getTeamKey() {
        return teamKey;
    }

    public void setTeamKey(String teamKey) {
        this.teamKey = teamKey;
    }

    public String getTeamWebsite() {
        return teamWebsite;
    }

    public void setTeamWebsite(String teamWebsite) {
        this.teamWebsite = teamWebsite;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public int getTeamNumber() {
        return teamNumber;
    }

    public void setTeamNumber(int teamNumber) {
        this.teamNumber = teamNumber;
    }

    public ArrayList<String> getTeamEvents() {
        if(teamEvents == null)
            teamEvents = new ArrayList<String>();
        return teamEvents;
    }

    public void setTeamEvents(ArrayList<String> teamEvents) {
        this.teamEvents = teamEvents;
    }

    public void mergeEvents(ArrayList<String> moreEvents){
        teamEvents.addAll(moreEvents);
        Collections.sort(teamEvents);
        String last = "";
        for(int i=0;i<teamEvents.size();i++){
            if(teamEvents.get(i).equals(last)){
                last = teamEvents.get(i);
                teamEvents.remove(i);
            }else{
                last = teamEvents.get(i);
            }

        }
    }

    public void removeEvent(String eventKey){
        int index = teamEvents.indexOf(eventKey);
        if(index != -1){
            teamEvents.remove(index);
        }
    }

    public String buildTitle(boolean numNotes){
        int notes = StartActivity.db.getAllNotes(teamKey).size();
        return teamNumber+(numNotes&&notes>0?" ("+notes+" Notes)":"");
    }

    public static void setSortType(int type){
        compareType = type;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public int compareTo(Team team) {
        switch(compareType){
            default:
            case COMPARE_TEAM_NUMBER:
                return Integer.compare(teamNumber,team.teamNumber);
            case COMPARE_NUM_NOTES:
                return Integer.compare(StartActivity.db.getAllNotes(teamKey).size(),StartActivity.db.getAllNotes(team.getTeamKey()).size());
        }
    }
}
