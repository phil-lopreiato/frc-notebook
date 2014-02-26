package com.plnyyanks.frcnotebook.background;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.plnyyanks.frcnotebook.Constants;
import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.activities.StartActivity;
import com.plnyyanks.frcnotebook.activities.ViewEvent;
import com.plnyyanks.frcnotebook.adapters.ActionBarCallback;
import com.plnyyanks.frcnotebook.adapters.EventListArrayAdapter;
import com.plnyyanks.frcnotebook.datatypes.Event;
import com.plnyyanks.frcnotebook.datatypes.Note;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phil on 2/22/14.
 */
public class ShowLocalEvents extends AsyncTask<Activity,String,String> {

    private Activity parentActivity;
    private EventListArrayAdapter adapter;
    private ListView eventList;
    private Object mActionMode;
    private int selectedItem=-1;
    ArrayList<String> finalKeys = new ArrayList<String>(), finalEvents = new ArrayList<String>();


    @Override
    protected String doInBackground(Activity... activities) {
        parentActivity = activities[0];
        List<Event> storedEvents = StartActivity.db.getAllEvents();

        eventList = (ListView) parentActivity.findViewById(R.id.event_list);
        eventList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        Event e;
        for(int i=0;i<storedEvents.size();i++){
            e=storedEvents.get(i);
            finalEvents.add(e.getEventName() + " - "+e.getEventYear());
            finalKeys.add(e.getEventKey());
        }

        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter = new EventListArrayAdapter(parentActivity,finalEvents,finalKeys);
                eventList.setAdapter(adapter);
                eventList.setOnItemClickListener(new ClickListener());
                eventList.setOnItemLongClickListener(new LongClickListener());
                //eventList.setOnItemSelectedListener(new SelectedListener());
            }
        });

        return null;
    }




    private class ClickListener implements ListView.OnItemClickListener{

        public ClickListener(){
            super();
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Log.d(Constants.LOG_TAG,"Item click: "+i+", selected: "+selectedItem);

                String eventKey = finalKeys.get(i);
                ViewEvent.setEvent(eventKey);
                Intent intent = new Intent(parentActivity, ViewEvent.class);
                parentActivity.startActivity(intent);

        }
    }

    private class LongClickListener implements ListView.OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            Log.d(Constants.LOG_TAG, "Item Long Click: " + i);
            eventList.setOnItemClickListener(null);
            view.setSelected(true);
            adapter.notifyDataSetChanged();
            selectedItem = i;
            // start the CAB using the ActionMode.Callback defined above
            mActionMode = parentActivity.startActionMode(mActionModeCallback);
            return false;
        }
    }

    private class SelectedListener implements ListView.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            Log.d(Constants.LOG_TAG,"Item Selected: "+i);
            view.setBackgroundResource(android.R.color.holo_blue_light);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            adapterView.setBackgroundResource(android.R.color.transparent);
        }
    }

    private ActionMode.Callback mActionModeCallback = new ActionBarCallback() {
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    confirmAndDelete(selectedItem);
                    // the Action was executed, close the CAB
                    selectedItem = -1;
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            Log.d(Constants.LOG_TAG,"Destroy CAB");
            mActionMode = null;
            eventList.setOnItemClickListener(new ClickListener());
            eventList.requestFocusFromTouch();
            eventList.clearChoices();
            adapter.notifyDataSetChanged();
        }

        private void confirmAndDelete(final int item){
            AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);
            builder.setTitle("Confirm Deletion");
            builder.setMessage("Are you sure you want to delete " + finalKeys.get(item) + "?");
            builder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //delete the event now
                            StartActivity.db.deleteEvent(finalKeys.get(item));
                            Toast.makeText(parentActivity, "Deleted " + finalKeys.get(item) + " from database", Toast.LENGTH_SHORT).show();
                            adapter.removeAt(item);
                            adapter.notifyDataSetChanged();
                            dialog.cancel();
                        }
                    });

            builder.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            builder.create().show();
        }
    };
}

