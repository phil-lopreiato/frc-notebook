package com.plnyyanks.frcnotebook.datafeed;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.plnyyanks.frcnotebook.database.PreferenceHandler;

/**
 * File created by phil on 2/20/2014.
 * Copyright 2014, Phil Lopreiato
 * This file is part of FRC Notebook.
 * FRC Notebook is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * FRC Notebook is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with FRC Notebook. If not, see http://www.gnu.org/licenses/.
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
