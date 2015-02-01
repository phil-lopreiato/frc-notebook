package com.plnyyanks.frcnotebook.datafeed;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.plnyyanks.frcnotebook.background.ShowLocalEvents;

/**
 * File created by phil on 2/19/2014.
 * Copyright 2015, Phil Lopreiato
 * This file is part of FRC Notebook
 * FRC Notebook is licensed under the MIT License
 * (http://opensource.org/licenses/MIT)
 */
public class EventDetailFetcher extends AsyncTask<String,String,String> {

    private static Activity activity;
    private static String event;

    public EventDetailFetcher(Activity parentActivity, String eventKey){
        activity = parentActivity;
        event = eventKey;
    }

    @Override
    protected String doInBackground(String... strings) {
        return TBADatafeed.fetchEventDetails_TBAv2(event);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Toast.makeText(activity,s,Toast.LENGTH_SHORT).show();
        if(ShowLocalEvents.adapter!=null){
            ShowLocalEvents.adapter.notifyDataSetChanged();
        }
    }
}
