package com.plnyyanks.frcnotebook.datatypes;

import com.plnyyanks.frcnotebook.activities.StartActivity;

/**
 * File created by phil on 2/19/14.
 * Copyright 2015, Phil Lopreiato
 * This file is part of FRC Notebook
 * FRC Notebook is licensed under the MIT License
 * (http://opensource.org/licenses/MIT)
 */
public class Note {
    private String  eventKey;
    private String matchKey;
    private String teamKey;
    private String note;

    private String pictures;
    private short   id,parent;
    private long    timestamp;

    public Note(){
        timestamp = System.currentTimeMillis();
        parent = -1;
        pictures="";
    }

    public Note(String eventKey, String matchKey, String teamKey, String note){
        this(eventKey,matchKey,teamKey,note,(short)-1,"");
    }

    public Note(String eventKey, String matchKey, String teamKey, String note,short parent,String pictures) {
        this.eventKey = eventKey;
        this.matchKey = matchKey;
        this.teamKey = teamKey;
        this.note = note;
        this.timestamp = System.currentTimeMillis();
        this.parent = parent;
        this.pictures = pictures;
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

    public String getPictures() {
        return pictures;
    }

    public void setPictures(String pictures) {
        this.pictures = pictures;
    }

    public short getParent() {
        return parent;
    }

    public void setParent(short parent) {
        this.parent = parent;
    }

    public static String buildGeneralNoteTitle(Note note,boolean displayEvent,boolean displayTeam){
        String output = "";

        if(displayTeam && !note.getTeamKey().equals("all")){
            output += note.getTeamKey().substring(3)+": ";
        }

        if(displayEvent){
            //on all notes tab. Include event title
            Event parentEvent = StartActivity.db.getEvent(note.getEventKey());
            if(parentEvent!=null){
                //note is associated with an event
                output += parentEvent.getShortName()+": ";
            }
        }
        output += note.getNote();
        return output;
    }

    public static String buildMatchNoteTitle(Note note, boolean displayEvent, boolean displayMatch, boolean displayTeam){
       return buildMatchNoteTitle(note,displayEvent,displayMatch,displayTeam,false);
    }

    public static String buildMatchNoteTitle(Note note, boolean displayEvent, boolean displayMatch, boolean displayTeam,boolean lineBreak){
        String output = "";

        if(displayTeam && !note.getTeamKey().equals("all")){
            output += note.getTeamKey().substring(3)+", ";
        }

        if(displayEvent && !note.getEventKey().equals("all")){
            //on all notes tab. Include event title
            Event parentEvent = StartActivity.db.getEvent(note.getEventKey());
            output += parentEvent.getShortName()+" ";
        }

        if(displayMatch && !note.getMatchKey().equals("all")){
            Match parentMatch = StartActivity.db.getMatch(note.getMatchKey());
            if(parentMatch!=null)
                output += parentMatch.getTitle()+" ";
        }
        if(displayEvent||displayMatch||displayTeam){
            output += "- ";
        }
        if(lineBreak){
            output+="\n";
        }

        if(note.getParent()==-1){
            output +=note.getNote();
        }else{
            output += StartActivity.db.getDefNote(note.getParent());
        }

        return output;
    }
}
