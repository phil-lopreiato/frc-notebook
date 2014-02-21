package com.plnyyanks.frcvolhelper.datatypes;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by phil on 2/19/14.
 */
public class Event {

    private String  eventKey;
    private String eventName;

    private String shortName;
    private String eventLocation;
    private String eventStart;
    private String eventEnd;
    private int     eventYear;

    private ArrayList<Match> quals,quarterFinals,semiFinals,finals;

    public Event(){

    }

    public Event(String eventKey, String eventName, String shortName, String eventLocation, String eventStart, String eventEnd, int eventYear) {
        this.eventKey = eventKey;
        this.eventName = eventName;
        this.shortName = shortName;
        this.eventLocation = eventLocation;
        this.eventStart = eventStart;
        this.eventEnd = eventEnd;
        this.eventYear = eventYear;
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getEventStart() {
        return eventStart;
    }

    public void setEventStart(String eventStart) {
        this.eventStart = eventStart;
    }

    public String getEventEnd() {
        return eventEnd;
    }

    public void setEventEnd(String eventEnd) {
        this.eventEnd = eventEnd;
    }

    public int getEventYear() {
        return eventYear;
    }

    public void setEventYear(int eventYear) {
        this.eventYear = eventYear;
    }

    public ArrayList<Match> getQuals() {
        if(quals == null)
            quals = new ArrayList<Match>();
        return quals;
    }

    public ArrayList<Match> getQuarterFinals() {
        if(quarterFinals==null)
            quarterFinals = new ArrayList<Match>();
        return quarterFinals;
    }

    public ArrayList<Match> getSemiFinals() {
        if(semiFinals==null)
            semiFinals = new ArrayList<Match>();
        return semiFinals;
    }

    public ArrayList<Match> getFinals() {
        if(finals==null)
            finals = new ArrayList<Match>();
        return finals;
    }

    public void sortMatches(ArrayList<Match> allMatches){
        quals = new ArrayList<Match>();
        quarterFinals = new ArrayList<Match>();
        semiFinals = new ArrayList<Match>();
        finals = new ArrayList<Match>();

        String matchKey;
        for(Match m:allMatches){
            matchKey = m.getMatchKey();
            if(matchKey.contains("_qm")){
                //qualification match
                quals.add(m);
            }
            if(matchKey.contains("_qf")){
                //quarter final match
                quarterFinals.add(m);
            }
            if(matchKey.contains("_sf")){
                //semifinal match
                semiFinals.add(m);
            }
            if(matchKey.contains("_f")){
                //final match
                finals.add(m);
            }
        }
        Collections.sort(quals);
        Collections.sort(quarterFinals);
        Collections.sort(semiFinals);
        Collections.sort(finals);
    }
}
