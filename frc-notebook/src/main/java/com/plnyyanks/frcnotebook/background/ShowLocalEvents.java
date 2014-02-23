package com.plnyyanks.frcnotebook.background;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.plnyyanks.frcnotebook.Constants;
import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.activities.StartActivity;
import com.plnyyanks.frcnotebook.activities.ViewEvent;
import com.plnyyanks.frcnotebook.adapters.EventListArrayAdapter;
import com.plnyyanks.frcnotebook.datatypes.Event;

import java.util.List;

/**
 * Created by phil on 2/22/14.
 */
public class ShowLocalEvents extends AsyncTask<Activity,String,String> {

    private Activity parentActivity;

    @Override
    protected String doInBackground(Activity... activities) {
        parentActivity = activities[0];
        List<Event> storedEvents = StartActivity.db.getAllEvents();

        final ListView eventList = (ListView) parentActivity.findViewById(R.id.event_list);
        eventList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        String[] events = new String[storedEvents.size()],
                 keys = new String[storedEvents.size()];
        Event e;
        for(int i=0;i<storedEvents.size();i++){
            e=storedEvents.get(i);
            events[i] = e.getEventName() + " - "+e.getEventYear();
            keys[i] = e.getEventKey();
        }
        final String[]  finalKeys = keys,
                        finalEvents = events;

        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                EventListArrayAdapter adapter = new EventListArrayAdapter(parentActivity,finalEvents,finalKeys);
                eventList.setAdapter(adapter);
                eventList.setOnItemClickListener(new ClickListener(finalKeys));
                eventList.setOnLongClickListener(new LongClickListener());
            }
        });

        return null;
    }




    private class ClickListener implements ListView.OnItemClickListener{

        final String[] keys;
        int pos;

        public ClickListener(String[] eventKeys){
            keys = eventKeys;
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String eventKey = keys[i];
            ViewEvent.setEvent(eventKey);
            Intent intent = new Intent(parentActivity, ViewEvent.class);
            parentActivity.startActivity(intent);
        }
    }

    private class LongClickListener implements ListView.OnItemLongClickListener, View.OnLongClickListener {

        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            return false;
        }

        @Override
        public boolean onLongClick(View view) {
            return false;
        }
    }
}
