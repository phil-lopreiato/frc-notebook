package com.plnyyanks.frcnotebook.datafeed;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.adapters.ListViewArrayAdapter;
import com.plnyyanks.frcnotebook.datatypes.Event;
import com.plnyyanks.frcnotebook.datatypes.ListElement;
import com.plnyyanks.frcnotebook.datatypes.ListHeader;
import com.plnyyanks.frcnotebook.datatypes.ListItem;
import com.plnyyanks.frcnotebook.json.JSONManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by phil on 2/18/14.
 */
public class EventListFetcher extends AsyncTask<Activity,String,JsonArray>{

    private Activity listActivity;
    private String year;
    private ListViewArrayAdapter adapter;

    @Override
    protected JsonArray doInBackground(Activity... args) {

        listActivity = args[0];
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(listActivity);
        year = prefs.getString("competition_season","2014");
        String data = GET_Request.getWebData("http://www.thebluealliance.com/api/v1/events/list?year=" + year,true);
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

        JsonObject element;
        String eventName,eventKey;
        ArrayList<ListItem> events = new ArrayList<ListItem>();
        ArrayList<String>   keys = new ArrayList<String>();
        ArrayList<Event>    list = new ArrayList<Event>();
        for(int i=0;i<result.size()&&iterator.hasNext();i++){
            element = iterator.next().getAsJsonObject();
            eventName = element.get("name").getAsString();
            eventKey = element.get("key").getAsString();

            Event e = new Event();
            e.setEventKey(eventKey);
            e.setEventName(eventName);
            e.setEventStart(element.get("start_date").getAsString());
            list.add(e);
        }

        Collections.sort(list);
        int eventWeek = Integer.parseInt(Event.weekFormatter.format(new Date())),
                currentWeek;
        for(Event e:list){
            currentWeek = e.getCompetitionWeek();
            if(eventWeek != currentWeek){
                String header;
                if(currentWeek ==  9){
                    header = year + " Championship Event";
                }else{
                    header = year + " Week " + currentWeek;
                }
                events.add(new ListHeader(header));
                keys.add("week"+currentWeek);
            }
            eventWeek = currentWeek;

            events.add(new ListElement(e.getEventName(),e.getEventKey()));
            keys.add(e.getEventKey());
        }


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