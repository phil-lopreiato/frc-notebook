package com.plnyyanks.frcnotebook.tba;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.plnyyanks.frcnotebook.Constants;
import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.adapters.EventListArrayAdapter;
import com.plnyyanks.frcnotebook.json.JSONManager;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by phil on 2/18/14.
 */
public class TBA_EventFetcher extends AsyncTask<Activity,String,JsonArray>{

    private Activity listActivity;
    private String year;

    @Override
    protected JsonArray doInBackground(Activity... args) {

        listActivity = args[0];
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(listActivity);
        year = prefs.getString("competition_season","2014");
        String data = GET_Request.getWebData("http://www.thebluealliance.com/api/v1/events/list?year="+year);
        return JSONManager.getasJsonArray(data);
    }

    @Override
    protected void onPostExecute(JsonArray result) {
        super.onPostExecute(result);
        //Log.d(Constants.LOG_TAG,"Event Data: "+result.toString());

        //now, add the events to the event picker activity
        Iterator<JsonElement> iterator = result.iterator();

        //LinearLayout eventList = (LinearLayout) listActivity.findViewById(R.id.event_list_to_download);
        ListView eventList = (ListView) listActivity.findViewById(R.id.event_list_to_download);

        JsonElement element;
        String eventName;
        ArrayList<String>   events = new ArrayList<String>(),
                            keys = new ArrayList<String>();
        for(int i=0;i<result.size()&&iterator.hasNext();i++){
            element = iterator.next();
            eventName = element.getAsJsonObject().get("name").getAsString();
            eventName += " - "+year;
            events.add(eventName);
            keys.add(element.getAsJsonObject().get("key").getAsString());
        }

        EventListArrayAdapter adapter = new EventListArrayAdapter(listActivity,events,keys);
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
                        new TBA_EventDetailFetcher(listActivity,keys.get(pos)).execute("");
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
    }
}