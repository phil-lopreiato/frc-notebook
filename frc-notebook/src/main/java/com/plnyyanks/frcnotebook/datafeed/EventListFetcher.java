package com.plnyyanks.frcnotebook.datafeed;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.adapters.ListViewArrayAdapter;
import com.plnyyanks.frcnotebook.database.PreferenceHandler;
import com.plnyyanks.frcnotebook.datatypes.ListHeader;
import com.plnyyanks.frcnotebook.datatypes.ListItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * File created by phil on 2/18/2014.
 * Copyright 2015, Phil Lopreiato
 * This file is part of FRC Notebook
 * FRC Notebook is licensed under the MIT License
 * (http://opensource.org/licenses/MIT)
 */
public class EventListFetcher extends AsyncTask<Activity,String,String>{

    private Activity listActivity;
    private String year;
    private ListViewArrayAdapter adapter;
    private ArrayList<ListItem> events;
    private ArrayList<String>   keys;

    @Override
    protected String doInBackground(Activity... args) {

        listActivity = args[0];
        year = PreferenceHandler.getYear();
        LinkedHashMap<String,ListItem> data;
        data = TBADatafeed.fetchEvents_TBAv2(year);

        keys = new ArrayList<>();
        keys.addAll(data.keySet());

        events = new ArrayList<>();
        events.addAll(data.values());

        return "";
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        //Log.d(Constants.LOG_TAG,"Event Data: "+result.toString());

        //LinearLayout eventList = (LinearLayout) listActivity.findViewById(R.id.event_list_to_download);
        ListView eventList = (ListView) listActivity.findViewById(R.id.event_list_to_download);

        adapter = new ListViewArrayAdapter(listActivity,events,keys);
        eventList.setAdapter(adapter);
        eventList.setOnItemClickListener(new EventClickListener(keys));

        //hide the progress bar
        ProgressBar prog = (ProgressBar) listActivity.findViewById(R.id.event_loading_progress);
        prog.setVisibility(View.GONE);
    }

    private class EventClickListener implements AdapterView.OnItemClickListener {

        final ArrayList<String> keys;
        int pos;

        public EventClickListener(ArrayList<String> eventKeys){
            keys = eventKeys;
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            ListItem item = adapter.values.get(i);
            if (item instanceof ListHeader) return;
            pos = i;
            AlertDialog.Builder builder = new AlertDialog.Builder(listActivity);
            DialogInterface.OnClickListener dialogClickListener = new DialogClickListener();

            builder.setMessage("Do you want to download info for "+keys.get(pos)+"?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
        }

        private class DialogClickListener implements DialogInterface.OnClickListener {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //YES! Download info...
                        Toast.makeText(listActivity, "Downloading Info for "+keys.get(pos),Toast.LENGTH_SHORT).show();
                        //start the background task to download matches
                        new EventDetailFetcher(listActivity,keys.get(pos)).execute("");
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
    }
}