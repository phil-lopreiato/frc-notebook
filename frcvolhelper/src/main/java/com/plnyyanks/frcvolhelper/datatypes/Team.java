package com.plnyyanks.frcvolhelper.datatypes;

import java.util.ArrayList;

/**
 * Created by phil on 2/19/14.
 */
public class Team {
    private String              teamKey;
    private int                 teamNumber;
    private ArrayList<String>   teamEvents;

    public Team(){

    }

    public Team(String teamKey, int teamNumber) {
        this.teamKey = teamKey;
        this.teamNumber = teamNumber;
    }

    public Team(String teamKey, int teamNumber, ArrayList<String> teamEvents) {
        this.teamKey = teamKey;
        this.teamNumber = teamNumber;
        this.teamEvents = teamEvents;
    }

    public Team(String teamKey, int teamNumber, String event) {
        this.teamKey = teamKey;
        this.teamNumber = teamNumber;
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

    public int getTeamNumber() {
        return teamNumber;
    }

    public void setTeamNumber(int teamNumber) {
        this.teamNumber = teamNumber;
    }

    public ArrayList<String> getTeamEvents() {
        return teamEvents;
    }

    public void setTeamEvents(ArrayList<String> teamEvents) {
        this.teamEvents = teamEvents;
    }
}
