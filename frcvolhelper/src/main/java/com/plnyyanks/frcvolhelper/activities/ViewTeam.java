package com.plnyyanks.frcvolhelper.activities;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import com.plnyyanks.frcvolhelper.R;
import com.plnyyanks.frcvolhelper.datatypes.Event;
import com.plnyyanks.frcvolhelper.datatypes.Team;

import java.util.ArrayList;

public class ViewTeam extends Activity implements ActionBar.TabListener {

    protected static String teamKey;
    private static int teamNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_team);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .commit();
        }

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
            Event event = StartActivity.db.getEvent(eventKey);
            ActionBar.Tab eventTab = bar.newTab();
            eventTab.setTag(event.getEventKey());
            eventTab.setText(event.getShortName());
            eventTab.setTabListener(this);
            bar.addTab(eventTab);
        }
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
       getFragmentManager().beginTransaction().replace(R.id.team_view, new EventFragment((String) tab.getTag())).commit();
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    public static class EventFragment extends Fragment{

        private String eventKey;

        public EventFragment(String key){
            eventKey = key;
        }

        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_event_tab, null);
            return v;
        }
    }
}
