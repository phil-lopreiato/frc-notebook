package com.plnyyanks.frcnotebook.datafeed;

import android.util.Log;

import com.plnyyanks.frcnotebook.Constants;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by phil on 2/18/14.
 */
public class GET_Request {
    public static String getWebData(String url, boolean tbaheader){

        InputStream is = null;
        String result = "";

        // HTTP
        try {
            HttpClient httpclient = new DefaultHttpClient(); // for port 80 requests!
            HttpGet httpget = new HttpGet(url);
            if(tbaheader)
                httpget.addHeader(Constants.TBA_HEADER, Constants.TBA_HEADER_TEXT);
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
        } catch(Exception e) {
            Log.e(Constants.LOG_TAG, e.toString());
            return null;
        }

        // Read response to string
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"),8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
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
