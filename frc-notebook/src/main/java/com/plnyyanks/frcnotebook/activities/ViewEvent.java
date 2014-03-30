package com.plnyyanks.frcnotebook.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.background.GetEventMatches;
import com.plnyyanks.frcnotebook.background.GetTeamsAttending;
import com.plnyyanks.frcnotebook.database.PreferenceHandler;
import com.plnyyanks.frcnotebook.datatypes.Event;
import com.plnyyanks.frcnotebook.tba.TBA_EventDetailFetcher;

public class ViewEvent extends Activity implements ActionBar.TabListener {

    private static String key;
    private static Event event;
    protected static Activity activity;
    private static int tabPosition=0;

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

        bar.setSelectedNavigationItem(tabPosition);
    }

    @Override
    protected void onResume() {
        StartActivity.checkThemeChanged(ViewEvent.class);
        GetEventMatches.setActivity(this);
        super.onResume();
        getActionBar().setSelectedNavigationItem(tabPosition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id){
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_update_event:
                Toast.makeText(this,"Updating data for "+key,Toast.LENGTH_SHORT).show();
                new TBA_EventDetailFetcher(this, key).execute("");
                return true;
            case R.id.action_view_tba:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://thebluealliance.com/event/" + key)));
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
        tabPosition = tab.getPosition();
        switch(tabPosition){
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
