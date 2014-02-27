package com.plnyyanks.frcnotebook.activities;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.plnyyanks.frcnotebook.Constants;
import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.background.GetNotesForTeam;
import com.plnyyanks.frcnotebook.database.PreferenceHandler;
import com.plnyyanks.frcnotebook.datatypes.Event;
import com.plnyyanks.frcnotebook.datatypes.Note;
import com.plnyyanks.frcnotebook.datatypes.Team;

import java.util.ArrayList;

public class ViewTeam extends Activity implements ActionBar.TabListener {

    protected static String teamKey, eventName;
    protected static int teamNumber;
    protected static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(PreferenceHandler.getTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_team);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .commit();
        }

        activity = this;

        ActionBar bar = getActionBar();
        bar.setTitle("Team "+teamNumber);

        //tab for team overview
        ActionBar.Tab teamOverviewTab = bar.newTab();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        teamOverviewTab.setText("All Notes");
        teamOverviewTab.setTag("all");
        teamOverviewTab.setTabListener(this);
        bar.addTab(teamOverviewTab);

        //add an actionbar tab for every event the team is competing at
        Team team = StartActivity.db.getTeam(teamKey);
        ArrayList<String> events = team.getTeamEvents();
        for(String eventKey:events){
            Log.d(Constants.LOG_TAG, "Making AB Tab for " + eventKey);
            Event event = StartActivity.db.getEvent(eventKey);
            ActionBar.Tab eventTab = bar.newTab();
            eventTab.setTag(event.getEventKey());
            eventTab.setText(event.getShortName());
            eventTab.setTabListener(this);
            bar.addTab(eventTab);
        }

    }

    @Override
    protected void onResume() {
        StartActivity.checkThemeChanged(ViewTeam.class);
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_team, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void setTeam(String key){
        teamKey = key;
        teamNumber = Integer.parseInt(key.substring(3));
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        eventName = tab.getTag().toString();
       getFragmentManager().beginTransaction().replace(R.id.team_view, new EventFragment((String) tab.getTag())).commit();
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    public static class EventFragment extends Fragment{

        private static String eventKey;
        private static View thisView;

        public EventFragment(String key){
            super();
            eventKey = key;
        }

        public EventFragment(){

        }

        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View v = inflater.inflate(R.layout.fragment_event_tab, null);
            thisView = v;
            new GetNotesForTeam(activity).execute(teamKey,eventKey,eventName);
            return v;
        }
    }
}
