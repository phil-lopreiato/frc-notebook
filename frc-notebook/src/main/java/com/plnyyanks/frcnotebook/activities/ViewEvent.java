package com.plnyyanks.frcnotebook.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.plnyyanks.frcnotebook.Constants;
import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.background.GetEventMatches;
import com.plnyyanks.frcnotebook.background.GetTeamsAttending;
import com.plnyyanks.frcnotebook.database.PreferenceHandler;
import com.plnyyanks.frcnotebook.datafeed.EventDetailFetcher;
import com.plnyyanks.frcnotebook.datatypes.Event;
import com.plnyyanks.frcnotebook.dialogs.InputURLForMatchesDialog;

/**
 * File created by phil on 3/1/14.
 * Copyright 2015, Phil Lopreiato
 * This file is part of FRC Notebook
 * FRC Notebook is licensed under the MIT License
 * (http://opensource.org/licenses/MIT)
 */
public class ViewEvent extends Activity implements ActionBar.TabListener {

    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
    public static final int ID_ADD_MATCHES = 1124;

    private static String key;
    private static Event event;
    protected static Activity activity;

    public static void setEvent(String eventKey){
        key = eventKey;
        event = StartActivity.db.getEvent(key);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(PreferenceHandler.getTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        activity = this;

        if(event==null){
            Intent intent = new Intent(this, StartActivity.class);
            startActivity(intent);
            return;
        }


        ActionBar bar = getActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.setTitle(event.getEventName());
        bar.setSubtitle("#"+key);
        bar.setDisplayHomeAsUpEnabled(true);

        //tab for team list
        ActionBar.Tab teamListTab = bar.newTab();
        teamListTab.setText("Teams Attending");
        teamListTab.setTabListener(this);
        bar.addTab(teamListTab);

        //tab for match schedule
        ActionBar.Tab scheduleTab = bar.newTab();
        scheduleTab.setText("Match Schedule");
        scheduleTab.setTabListener(this);
        bar.addTab(scheduleTab);

        if(savedInstanceState!=null) {
            bar.setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM,0));
        }else{
            bar.setSelectedNavigationItem(0);
        }
    }

    @Override
    protected void onResume() {
        StartActivity.checkThemeChanged(ViewEvent.class);
        GetEventMatches.setActivity(this);
        super.onResume();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current dropdown position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            try {
                getActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
            }catch(IllegalStateException e){
                Log.w(Constants.LOG_TAG,"Failed restoring action bar navegition state on resume. Oh well...");
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current dropdown position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
                .getSelectedNavigationIndex());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_event, menu);

        if(event != null && !event.isOfficial()){
            //allow updating of scores via URL for unofficial events, but not for official ones
            menu.add(0,ID_ADD_MATCHES,Menu.NONE,R.string.action_add_matches);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id){
            case R.id.action_add_collaborator:
                startActivity(CollaboratorsActivity.newInstance(this, key));
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_update_event:
                Toast.makeText(this,"Updating data for "+key,Toast.LENGTH_SHORT).show();
                new EventDetailFetcher(this, key).execute("");
                return true;
            case R.id.action_view_tba:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://thebluealliance.com/event/" + key)));
                return true;
            case ID_ADD_MATCHES:
                new InputURLForMatchesDialog(getString(R.string.title_add_matches),event).show(getFragmentManager(),"addMatchesFromURL");
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        Fragment f;
        switch(tab.getPosition()){
            case 0:
            default:
                f = new EventTeamListFragment(); break;
            case 1:
                f = new EventScheduleFragment(); break;
        }

        getFragmentManager().beginTransaction().replace(R.id.event_view,f).commit();

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    public static class EventTeamListFragment extends Fragment {

        public EventTeamListFragment(){
            super();
        }

        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_event_team_list, null);
            new GetTeamsAttending(activity).execute(key);
            return v;
        }
    }

    public static class EventScheduleFragment extends Fragment{
        View view;

        public EventScheduleFragment(){
            super();
        }

        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_event_schedule, null);
            view = v;
            //loadMatchList();
            new GetEventMatches(activity).execute(key);
            return v;
        }

        @Override
        public void onResume() {
            super.onResume();
        }

        class MatchClickHandler implements View.OnClickListener{

            @Override
            public void onClick(View view) {
                ViewMatch.setMatchKey((String)view.getTag());

                Intent intent = new Intent(activity, ViewMatch.class);
                startActivity(intent);
            }
        }
    }

}
