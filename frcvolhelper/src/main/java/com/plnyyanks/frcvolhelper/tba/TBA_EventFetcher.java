package com.plnyyanks.frcvolhelper.tba;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonArray;
import com.plnyyanks.frcvolhelper.Constants;
import com.plnyyanks.frcvolhelper.json.JSONManager;

/**
 * Created by phil on 2/18/14.
 */
public class TBA_EventFetcher extends AsyncTask<String,String,JsonArray>{

    @Override
    protected JsonArray doInBackground(String... args) {

        String data = GET_Request.getWebData(args[0]);
        return JSONManager.eventStringtoArray(data);
    }

    @Override
    protected void onPostExecute(JsonArray result) {
        super.onPostExecute(result);
        Log.d(Constants.LOG_TAG,"Event Data: "+result.toString());
        //now, add the events to the event picker activity
    }
}
