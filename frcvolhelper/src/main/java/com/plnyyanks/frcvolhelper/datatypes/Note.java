package com.plnyyanks.frcvolhelper.datatypes;

/**
 * Created by phil on 2/19/14.
 */
public class Note {
    private String  eventKey,
                    matchKey,
                    teamKey,
                    note;
    private short   id;
    private long    timestamp;

    public Note(){
        timestamp = System.currentTimeMillis();
    }

    public Note(String eventKey, String matchKey, String teamKey, String note) {
        this.id = id;
        this.eventKey = eventKey;
        this.matchKey = matchKey;
        this.teamKey = teamKey;
        this.note = note;
        this.timestamp = System.currentTimeMillis();
    }

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
