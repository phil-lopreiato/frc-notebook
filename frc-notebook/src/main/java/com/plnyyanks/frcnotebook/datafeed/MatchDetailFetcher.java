package com.plnyyanks.frcnotebook.datafeed;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.plnyyanks.frcnotebook.database.PreferenceHandler;

/**
 * File created by phil on 2/20/2014.
 * Copyright 2015, Phil Lopreiato
 * This file is part of FRC Notebook
 * FRC Notebook is licensed under the MIT License
 * (http://opensource.org/licenses/MIT)
 */
public class MatchDetailFetcher extends AsyncTask<String,String,String>{

    private Context context;
    private String eventKey;

    public MatchDetailFetcher(Context inContext, String eventKey){
        this.context = inContext;
        this.eventKey = eventKey;
    }

    @Override
    protected String doInBackground(String... strings) {
        switch(PreferenceHandler.getDataSource()) {
            //PJL 20150131 - Only TBAv2 is allowed as a data source
            /*case TBAv1:
                TBADatafeed.fetchMatches_TBAv1(strings[0]);
                break;
            */
            default:
            case TBAv2:
                TBADatafeed.fetchMatches_TBAv2(strings[0]);
                break;
            /*
            case USFIRST:
            default:
                USFIRSTParser.fetchMatches_USFIRST(strings[1].substring(0,4),strings[1].substring(4));
                break;
            */
        }
        return "";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        //we've finished. now toast the user
        if(s.equals(""))
            Toast.makeText(context, "Info downloaded for " + this.eventKey, Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }


}
