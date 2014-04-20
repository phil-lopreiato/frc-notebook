package com.plnyyanks.frcnotebook.datafeed;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.plnyyanks.frcnotebook.Constants;
import com.plnyyanks.frcnotebook.activities.StartActivity;
import com.plnyyanks.frcnotebook.background.ShowLocalEvents;
import com.plnyyanks.frcnotebook.database.PreferenceHandler;
import com.plnyyanks.frcnotebook.datatypes.Event;
import com.plnyyanks.frcnotebook.datatypes.Team;
import com.plnyyanks.frcnotebook.json.JSONManager;

import java.util.Iterator;

/**
 * Created by phil on 2/19/14.
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
        if(PreferenceHandler.getDataSource() == Constants.DATAFEED_SOURCES.TBAv1)
            return TBADatafeed.fetchEventDetails_TBAv1(event);
        else
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
