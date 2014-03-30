package com.plnyyanks.frcnotebook.background;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.ActionMode;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.plnyyanks.frcnotebook.Constants;
import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.activities.StartActivity;
import com.plnyyanks.frcnotebook.activities.ViewEvent;
import com.plnyyanks.frcnotebook.activities.ViewTeam;
import com.plnyyanks.frcnotebook.adapters.ActionBarCallback;
import com.plnyyanks.frcnotebook.adapters.ListViewArrayAdapter;
import com.plnyyanks.frcnotebook.datatypes.Event;
import com.plnyyanks.frcnotebook.datatypes.ListElement;
import com.plnyyanks.frcnotebook.datatypes.ListHeader;
import com.plnyyanks.frcnotebook.datatypes.ListItem;
import com.plnyyanks.frcnotebook.dialogs.DeleteDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ShowLocalEvents extends AsyncTask<Activity,String,String> {

    private Activity parentActivity;
    private ListViewArrayAdapter adapter;
    private ListView eventList;
    //private Object mActionMode;
    private int selectedItem=-1;
    ArrayList<String> finalKeys = new ArrayList<String>();
    ArrayList<ListItem> finalEvents = new ArrayList<ListItem>();
    private boolean allEvents;

    public ShowLocalEvents(){
        allEvents = false;
    }

    public ShowLocalEvents(int selectedIndex){
        allEvents = selectedIndex==0;
    }

    @Override
    protected String doInBackground(Activity... activities) {
        parentActivity = activities[0];
        final List<Event> storedEvents = allEvents?StartActivity.db.getAllEvents():StartActivity.db.getCurrentEvents();
        Collections.sort(storedEvents);

        eventList = (ListView) parentActivity.findViewById(R.id.event_list);
        eventList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        int eventWeek = Integer.parseInt(Event.weekFormatter.format(new Date())),
            currentWeek;
        if(storedEvents.size()==0){
            finalEvents.add(new ListElement(allEvents?parentActivity.getString(R.string.no_events_message):parentActivity.getString(R.string.no_events_this_week_message),"-1"));
            finalKeys.add("-1");
        }else if(allEvents){
            finalEvents.add(new ListElement(parentActivity.getString(R.string.view_all_notes_message),"all"));
            finalKeys.add("all");
        }
        for (Event e : storedEvents) {
            currentWeek = e.getCompetitionWeek();
            if (eventWeek != currentWeek) {
                finalEvents.add(new ListHeader(e.getEventYear() + " Week " + currentWeek));
                finalKeys.add(e.getEventYear() + "_week" + currentWeek);
            }
            eventWeek = currentWeek;

            finalEvents.add(new ListElement(e.getEventName(), e.getEventKey()));
            finalKeys.add(e.getEventKey());
        }

        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter = new ListViewArrayAdapter(parentActivity,finalEvents,finalKeys);
                eventList.setAdapter(adapter);
                if(storedEvents.size()!=0){
                    eventList.setOnItemClickListener(new ClickListener());
                    eventList.setOnItemLongClickListener(new LongClickListener());
                }
                //eventList.setOnItemSelectedListener(new SelectedListener());
                //hide the progress bar
                ProgressBar prog = (ProgressBar) parentActivity.findViewById(R.id.event_list_loading_progress);
                prog.setVisibility(View.GONE);
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
            Log.d(Constants.LOG_TAG, "Item click: " + i + ", selected: " + selectedItem);
            ListItem item = adapter.values.get(i);
            if (item instanceof ListHeader || ((ListElement)item).getKey().equals("-1")) return;

            String eventKey = finalKeys.get(i);
            Intent intent;
            if(eventKey.equals("all")){
                ViewTeam.setTeam("all");
                intent = new Intent(parentActivity, ViewTeam.class);
            }else{
                ViewEvent.setEvent(eventKey);
                intent = new Intent(parentActivity, ViewEvent.class);
            }
            parentActivity.startActivity(intent);
        }
    }

    private class LongClickListener implements ListView.OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            Log.d(Constants.LOG_TAG, "Item Long Click: " + i);
            ListItem item =  adapter.values.get(i);
            if(item instanceof ListHeader || ((ListElement)item).getKey().equals("-1") || ((ListElement)item).getKey().equals("all")) return false;

            eventList.setOnItemClickListener(null);
            item.setSelected(true);
            view.setSelected(true);
            adapter.notifyDataSetChanged();
            selectedItem = i;
            // start the CAB using the ActionMode.Callback defined above
            parentActivity.startActionMode(mActionModeCallback);
            return false;
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
            //mActionMode = null;
            eventList.setOnItemClickListener(new ClickListener());
            eventList.requestFocusFromTouch();
            eventList.clearChoices();
            adapter.notifyDataSetChanged();
        }

        private void confirmAndDelete(final int item){
            DialogInterface.OnClickListener deleter =
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //delete the event now
                            Toast.makeText(parentActivity, "Deleting " + finalKeys.get(item), Toast.LENGTH_SHORT).show();
                            new DeleteEvent(parentActivity).execute(finalKeys.get(item));
                            adapter.removeAt(item);
                            adapter.notifyDataSetChanged();
                            dialog.cancel();
                        }
                    };
            new DeleteDialog(parentActivity.getString(R.string.delete_event_message)+finalKeys.get(item)+"?",deleter)
                    .show(parentActivity.getFragmentManager(), "delete_event");
        }
    };
}

