package com.plnyyanks.frcnotebook.background;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.plnyyanks.frcnotebook.Constants;
import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.activities.StartActivity;
import com.plnyyanks.frcnotebook.activities.ViewTeam;
import com.plnyyanks.frcnotebook.adapters.ListViewArrayAdapter;
import com.plnyyanks.frcnotebook.datatypes.ListElement;
import com.plnyyanks.frcnotebook.datatypes.ListItem;
import com.plnyyanks.frcnotebook.datatypes.Team;

import java.util.ArrayList;
import java.util.Collections;

public class GetTeamsAttending extends AsyncTask<String,String,String> {

    private Activity activity;
    private String msg = "",eventKey;
    private static ArrayList<ListItem> teams;
    private static ArrayList<String> keys;
    private static ListViewArrayAdapter adapter;

    public GetTeamsAttending(Activity activity){
        super();
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... strings) {
        eventKey = strings[0];

        ArrayList<Team> teamList = StartActivity.db.getAllTeamAtEvent(eventKey);
        Team.setSortType(Team.COMPARE_TEAM_NUMBER);
        Collections.sort(teamList);

        teams = new ArrayList<ListItem>();
        keys  = new ArrayList<String>();
        for (Team t : teamList) {
            teams.add(new ListElement(t.buildTitle(true), t.getTeamKey()));
            keys.add(t.getTeamKey());
        }

        final String[] sortValues = activity.getResources().getStringArray(R.array.team_list_sort_options);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter = new ListViewArrayAdapter(activity,teams,keys);
                ListView teamListView = (ListView) activity.findViewById(R.id.team_list);

                //create an adapter for the spinner
                ArrayAdapter<String> sortAdapter = new ArrayAdapter<String>(activity,android.R.layout.simple_spinner_item,android.R.id.text1,sortValues);
                sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                Spinner sortSpinner = (Spinner)activity.findViewById(R.id.team_list_sort);

                if(teamListView != null && sortSpinner != null){
                    teamListView.setAdapter(adapter);
                    teamListView.setOnItemClickListener(new ClickListener(keys));

                    sortSpinner.setAdapter(sortAdapter);
                    sortSpinner.setOnItemSelectedListener(new SortListener());

                    //hide the progress bar
                    ProgressBar prog = (ProgressBar) activity.findViewById(R.id.teams_loading_progress);
                    prog.setVisibility(View.GONE);
                }else{
                    msg = activity.getString(R.string.team_fetch_error);
                }
            }
        });

        return msg;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if(s!= null && !s.equals("")){
            Toast.makeText(activity, s, Toast.LENGTH_SHORT).show();
        }
    }

    private class ClickListener implements ListView.OnItemClickListener{

        final ArrayList<String> keys;

        public ClickListener(ArrayList<String> eventKeys){
            keys = eventKeys;
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String teamKey = keys.get(i);
            ViewTeam.setTeam(teamKey);
            Intent intent = new Intent(activity, ViewTeam.class);
            activity.startActivity(intent);
        }
    }

    private class SortListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            ArrayList<Team> teamList = StartActivity.db.getAllTeamAtEvent(eventKey);
            switch(i){
                default:
                case 0:
                    //sort by team number
                    Team.setSortType(Team.COMPARE_TEAM_NUMBER);
                    Collections.sort(teamList);

                    break;
                case 1:
                    //sort by notes, asc
                    Team.setSortType(Team.COMPARE_NUM_NOTES);
                    Collections.sort(teamList);
                    break;
                case 2:
                    //sort by notes, desc
                    Team.setSortType(Team.COMPARE_NUM_NOTES);
                    Collections.sort(teamList);
                    Collections.reverse(teamList);
                    break;
            }

            teams = new ArrayList<ListItem>();
            keys  = new ArrayList<String>();
            for (Team t : teamList) {
                teams.add(new ListElement(t.buildTitle(true), t.getTeamKey()));
                keys.add(t.getTeamKey());
            }
            adapter = new ListViewArrayAdapter(activity,teams,keys);
            ListView teamListView = (ListView) activity.findViewById(R.id.team_list);
            teamListView.setAdapter(adapter);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }
}
