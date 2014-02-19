package com.plnyyanks.frcvolhelper.tba;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by phil on 2/19/14.
 */
public class TBA_EventDetailFetcher extends AsyncTask<String,String,String> {

    private static Activity activity;
    private static String event;

    public TBA_EventDetailFetcher(Activity parentActivity,String eventKey){
        activity = parentActivity;
        event = eventKey;
    }

    @Override
    protected String doInBackground(String... strings) {
        return GET_Request.getWebData("http://www.thebluealliance.com/api/v1/event/details?event="+event);
    }
}
