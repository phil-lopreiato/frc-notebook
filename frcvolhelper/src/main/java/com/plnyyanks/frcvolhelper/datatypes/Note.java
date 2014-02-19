package com.plnyyanks.frcvolhelper.datatypes;

/**
 * Created by phil on 2/19/14.
 */
public class Note {
    private String  eventKey,
                    matchKey,
                    teamKey,
                    note;

    public Note(String eventKey, String matchKey, String teamKey, String note) {
        this.eventKey = eventKey;
        this.matchKey = matchKey;
        this.teamKey = teamKey;
        this.note = note;
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    public String getMatchKey() {
        return matchKey;
    }

    public void setMatchKey(String matchKey) {
        this.matchKey = matchKey;
    }

    public String getTeamKey() {
        return teamKey;
    }

    public void setTeamKey(String teamKey) {
        this.teamKey = teamKey;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
