package com.plnyyanks.frcnotebook.background;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.plnyyanks.frcnotebook.datafeed.USFIRSTParser;

/**
 * File created by phil on 4/19/14.
 * Copyright 2014, Phil Lopreiato
 * This file is part of FRC Notebook.
 * FRC Notebook is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * FRC Notebook is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with FRC Notebook. If not, see http://www.gnu.org/licenses/.
 */
public class AddMatchesFromURL extends AsyncTask<String,String,String> {

    private Activity activity;

    public AddMatchesFromURL(Activity act){
        activity = act;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Toast.makeText(activity,"Fetching matches...",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected String doInBackground(String... params) {
        String url = params[0],eventKey = params[1];
        String[] parts  = url.split("\\.");
        String extension = parts[parts.length-1];
        if(extension.equals("html")){
            return USFIRSTParser.fetchMatchesFromURL(url, eventKey);
        }else if(extension.equals("csv")){
            //TODO CSV PARSING!
            return "csv Parsing coming soon";
        }else{
            return "Unsupported filetype. Use .html or .csv";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(!s.isEmpty()){
            Toast.makeText(activity,s,Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(activity,"Matches added",Toast.LENGTH_SHORT).show();
        }
    }
}
