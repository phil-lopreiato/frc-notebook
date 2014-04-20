package com.plnyyanks.frcnotebook.datatypes;

import android.util.Log;

import com.plnyyanks.frcnotebook.Constants;
import com.plnyyanks.frcnotebook.datafeed.USFIRSTParser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

/**
 * Created by phil on 2/19/14.
 */
public class Event implements Comparable<Event>{

    private String  eventKey;
    private String eventName;

    private String shortName;
    private String eventLocation;
    private String eventStart;
    private String eventEnd;
    private int    eventYear;
    private Date   startDate,
                   endDate;
    private boolean official;

    public static final DateFormat     df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH),
                                       df2= new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH);
    public static final SimpleDateFormat weekFormatter = new SimpleDateFormat("w");

    private ArrayList<Match> quals,quarterFinals,semiFinals,finals;

    public Event(){

    }

    public Event(String eventKey, String eventName, String shortName, String eventLocation, String eventStart, String eventEnd, int eventYear,boolean official) {
        this.eventKey = eventKey;
        this.eventName = eventName;
        this.shortName = shortName;
        this.eventLocation = eventLocation;
        this.eventStart = eventStart;
        this.eventEnd = eventEnd;
        this.eventYear = eventYear;
        this.official = official;

        try {
            startDate = parseDate(eventStart);
            endDate = parseDate(eventEnd);
            endDate.setTime(endDate.getTime() + 24*60*60*1000);
        } catch (ParseException e) {
            startDate = new Date();
            endDate = new Date();
            Log.e(Constants.LOG_TAG, "Unable to parse event date. " + Arrays.toString(e.getStackTrace()));
        }
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

    public Date getStartDate(){
        return startDate;
    }

    public void setEventStart(String eventStart) {
        this.eventStart = eventStart;
        try {
            startDate = parseDate(eventStart);
        } catch (ParseException e) {
            startDate = new Date();
            Log.e(Constants.LOG_TAG, "Unable to parse event date. " + Arrays.toString(e.getStackTrace()));
        }
    }

    public String getEventEnd() {
        return eventEnd;
    }

    public Date getEndDate(){
        return endDate;
    }

    public void setEventEnd(String eventEnd) {
        this.eventEnd = eventEnd;
        try {
            endDate = parseDate(eventEnd);
            endDate.setTime(endDate.getTime() + 24*60*60*1000);
        } catch (ParseException e) {
            endDate = new Date();
            Log.e(Constants.LOG_TAG, "Unable to parse event date. " + Arrays.toString(e.getStackTrace()));
        }
    }

    public boolean isHappeningNow(){
        Date now = new Date();
        return now.after(startDate) && now.before(endDate);
    }

    public int getEventYear() {
        return eventYear;
    }

    public void setEventYear(int eventYear) {
        this.eventYear = eventYear;
    }

    public void setOfficial(boolean official){
        this.official = official;
    }

    public boolean isOfficial(){
        return official;
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

    public int getCompetitionWeek(){
        if(startDate == null) return -1;
        int week = Integer.parseInt(weekFormatter.format(startDate))-8;
        return week<0?0:week;
    }

    public static int getCompetitionWeek(String dateString){
        try {
            Date eventDate = parseDate(dateString);
            int week = Integer.parseInt(weekFormatter.format(eventDate))-8;
            return week<0?0:week;
        } catch (ParseException e) {
            Log.e(Constants.LOG_TAG,"Unable to parse date string. "+Arrays.toString(e.getStackTrace()));
            return -1;
        }
    }

    private static Date parseDate(String dateString) throws ParseException {
        try {
            return df.parse(dateString);
        } catch (ParseException e) {
            Log.d(Constants.LOG_TAG,"TBAv1 date parse failed. Trying with v2 format...");
            return df2.parse(dateString);
        }
    }


    @Override
    public int compareTo(Event event) {
        if(startDate==null || event.getStartDate() == null)
            return 0;
        return startDate.compareTo(event.getStartDate());
    }
}
