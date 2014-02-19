package com.plnyyanks.frcvolhelper.tba;

/**
 * Created by phil on 2/18/14.
 */
public class TBA_API {
    public static void getEventsForSeason(String season){
      new TBA_EventFetcher().execute("http://www.thebluealliance.com/api/v1/events/list?year="+season);
    }
}
