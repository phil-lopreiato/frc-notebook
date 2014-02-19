package com.plnyyanks.frcvolhelper.tba;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.plnyyanks.frcvolhelper.Constants;
import com.plnyyanks.frcvolhelper.R;
import com.plnyyanks.frcvolhelper.json.JSONManager;

import java.util.Iterator;

/**
 * Created by phil on 2/18/14.
 */
public class TBA_EventFetcher extends AsyncTask<Activity,String,JsonArray>{

    private Activity listActivity;

    @Override
    protected JsonArray doInBackground(Activity... args) {
        listActivity = args[0];
        String data = GET_Request.getWebData("http://www.thebluealliance.com/api/v1/events/list?year=2014");
        return JSONManager.eventStringtoArray(data);
    }

    @Override
    protected void onPostExecute(JsonArray result) {
        super.onPostExecute(result);
        Log.d(Constants.LOG_TAG,"Event Data: "+result.toString());

        //now, add the events to the event picker activity
        Iterator<JsonElement> iterator = result.iterator();

        LinearLayout eventList = (LinearLayout) listActivity.findViewById(R.id.event_list_to_download);
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView test = new TextView(listActivity);
        JsonElement element;
        while(iterator.hasNext()){
            element = iterator.next();
            TextView tv=new TextView(listActivity);
            tv.setLayoutParams(lparams);
            tv.setText(element.getAsJsonObject().get("name").toString());
            eventList.addView(tv);
        }
    }
}
