package com.plnyyanks.frcnotebook.datafeed;

import com.plnyyanks.frcnotebook.Constants;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * File created by phil on 2/18/2014.
 * Copyright 2015, Phil Lopreiato
 * This file is part of FRC Notebook
 * FRC Notebook is licensed under the MIT License
 * (http://opensource.org/licenses/MIT)
 */
public class GET_Request {
    public static String getWebData(String url, boolean tbaheader){

        InputStream is;
        String result;

        // HTTP
        try {
            Log.d(Constants.LOG_TAG, "Fetching " + url);
            URL httpget = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) httpget.openConnection();
            if(tbaheader)
                urlConnection.addRequestProperty(Constants.TBA_HEADER, Constants.TBA_HEADER_TEXT);
            is = urlConnection.getInputStream();
        } catch(Exception e) {
            Log.e(Constants.LOG_TAG, e.toString());
            return null;
        }

        // Read response to string
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"),8);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            is.close();
            result = sb.toString();
        } catch(Exception e) {
            Log.e(Constants.LOG_TAG,e.toString());
            return null;
        }


        return result;

    }
}
