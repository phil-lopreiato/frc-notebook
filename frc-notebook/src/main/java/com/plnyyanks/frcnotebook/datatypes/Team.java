package com.plnyyanks.frcnotebook.datatypes;

import android.annotation.TargetApi;
import android.os.Build;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by phil on 2/19/14.
 */
public class Team implements Comparable<Team>{
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

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public int compareTo(Team team) {
        return Integer.compare(teamNumber,team.teamNumber);
    }
}
