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
        String[] events = new String[result.size()],
                 keys = new String[result.size()];
        for(int i=0;i<events.length&&iterator.hasNext();i++){
            element = iterator.next();
            eventName = element.getAsJsonObject().get("name").getAsString();
            eventName += " - "+year;
            /*TextView tv=new TextView(listActivity);
            tv.setLayoutParams(Constants.lparams);
            tv.setText("• " + eventName.substring(1, eventName.length() - 1)+ " - "+year);
            tv.setTextSize(20);
            tv.setClickable(true);
            tv.setTag(element.getAsJsonObject().get("key").toString());
            tv.setOnClickListener(new EventClickListener());
            eventList.addView(tv);*/
            events[i] = eventName;
            keys[i] = element.getAsJsonObject().get("key").getAsString();
        }

        EventListArrayAdapter adapter = new EventListArrayAdapter(listActivity,events,keys);
        eventList.setAdapter(adapter);
        eventList.setOnItemClickListener(new EventClickListener(keys));

        //hide the progress bar
        ProgressBar prog = (ProgressBar) listActivity.findViewById(R.id.event_loading_progress);
        prog.setVisibility(View.GONE);
    }

    private class EventClickListener implements AdapterView.OnItemClickListener {

        final String[] keys;
        int pos;

        public EventClickListener(String[] eventKeys){
            keys = eventKeys;
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            pos = i;
            AlertDialog.Builder builder = new AlertDialog.Builder(listActivity);
            DialogInterface.OnClickListener dialogClickListener = new DialogClickListener();

            builder.setMessage("Do you want to download info for "+keys[pos]+"?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
        }

        private class DialogClickListener implements DialogInterface.OnClickListener {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //YES! Download info...
                        Toast.makeText(listActivity, "Downloading Info for "+keys[pos],Toast.LENGTH_SHORT).show();
                        //start the background task to download matches
                        new TBA_EventDetailFetcher(listActivity,keys[pos]).execute("");
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
    }

    class EventListArrayAdapter extends ArrayAdapter<String>{
        private final Context context;
        private final String values[], keys[];

        public EventListArrayAdapter(Context context,String[] values,String[] keys){
            super(context,android.R.layout.simple_list_item_1,values);
            this.context = context;
            this.values = values;
            this.keys = keys;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            /*LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(android.R.layout.simple_list_item_1,parent,false);
            TextView mainLine = (TextView) rowView.findViewById(android.R.layout.simple_list_item_1); */
            return super.getView(position, convertView, parent);
        }

        public String getEventKey(int position){
            return keys[position];
        }
    }
}