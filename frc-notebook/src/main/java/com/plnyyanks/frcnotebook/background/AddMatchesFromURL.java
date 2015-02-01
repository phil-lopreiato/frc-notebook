package com.plnyyanks.frcnotebook.background;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.plnyyanks.frcnotebook.datafeed.USFIRSTParser;

/**
 * File created by phil on 4/19/14.
 * Copyright 2015, Phil Lopreiato
 * This file is part of FRC Notebook
 * FRC Notebook is licensed under the MIT License
 * (http://opensource.org/licenses/MIT)
 */
public class AddMatchesFromURL extends AsyncTask<String,String,String> {

    private Activity activity;

    public AddMatchesFromURL(Activity act){
        activity = act;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        try{
            Toast.makeText(activity,"Fetching matches...",Toast.LENGTH_SHORT).show();
        }catch(RuntimeException e){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity,"Fetching matches...",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected String doInBackground(String... params) {
        String url = params[0],eventKey = params[1];
        String[] parts  = url.split("\\.");
        String extension = parts[parts.length-1];
        if(extension.equals("html") || extension.equals("csv")){
            return USFIRSTParser.fetchMatchesFromURL(url, eventKey);
        }else{
            return "Unsupported filetype. Use .html or .csv";
        }
    }

    @Override
    protected void onPostExecute(final String s) {
        super.onPostExecute(s);
        try {
            if (!s.isEmpty()) {
                Toast.makeText(activity, s, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, "Matches added", Toast.LENGTH_SHORT).show();
            }
        }catch(RuntimeException e){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!s.isEmpty()) {
                        Toast.makeText(activity, s, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(activity, "Matches added", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
