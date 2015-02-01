package com.plnyyanks.frcnotebook.datafeed;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.plnyyanks.frcnotebook.background.ShowLocalEvents;

/**
 * File created by phil on 2/19/2014.
 * Copyright 2014, Phil Lopreiato
 * This file is part of FRC Notebook.
 * FRC Notebook is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * FRC Notebook is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with FRC Notebook. If not, see http://www.gnu.org/licenses/.
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
