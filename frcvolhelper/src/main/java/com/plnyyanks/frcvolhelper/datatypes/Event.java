package com.plnyyanks.frcvolhelper.datatypes;

/**
 * Created by phil on 2/19/14.
 */
public class Event {

    private String  eventKey,
                    eventName,
                    eventLocation,
                    eventStart,
                    eventEnd;
    private int     eventYear;

    public Event(){

    }

    public Event(String eventKey, String eventName, String eventLocation, String eventStart, String eventEnd, int eventYear) {
        this.eventKey = eventKey;
        this.eventName = eventName;
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
}
