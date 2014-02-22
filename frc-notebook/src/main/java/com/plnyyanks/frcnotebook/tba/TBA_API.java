package com.plnyyanks.frcnotebook.tba;

import android.app.Activity;

/**
 * Created by phil on 2/18/14.
 */
public class TBA_API {
    public static void getEventsForSeason(Activity parent){
      new TBA_EventFetcher().execute(parent);
    }
}
