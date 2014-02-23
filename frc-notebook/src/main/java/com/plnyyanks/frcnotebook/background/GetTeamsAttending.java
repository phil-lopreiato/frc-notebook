package com.plnyyanks.frcnotebook.background;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.activities.StartActivity;
import com.plnyyanks.frcnotebook.activities.ViewEvent;
import com.plnyyanks.frcnotebook.activities.ViewTeam;
import com.plnyyanks.frcnotebook.adapters.EventListArrayAdapter;
import com.plnyyanks.frcnotebook.datatypes.Event;
import com.plnyyanks.frcnotebook.datatypes.Team;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by phil on 2/23/14.
 */
public class GetTeamsAttending extends AsyncTask<String,String,String> {

    private Activity activity;

    public GetTeamsAttending(Activity activity){
        super();
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... strings) {
        final ListView teamListView = (ListView) activity.findViewById(R.id.team_list);
        String eventKey = strings[0];

        ArrayList<Team> teamList = StartActivity.db.getAllTeamAtEvent(eventKey);
        Collections.sort(teamList);
        String[] teams = new String[teamList.size()],
                 keys = new String[teamList.size()];
        Team t;
        for(int i=0;i<teamList.size();i++){
            t = teamList.get(i);
            teams[i] = Integer.toString(t.getTeamNumber());
            keys[i] = t.getTeamKey();
        }

        final String[]  finalKeys = keys,
                        finalTeams = teams;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                EventListArrayAdapter adapter = new EventListArrayAdapter(activity,finalTeams,finalKeys);
                teamListView.setAdapter(adapter);
                teamListView.setOnItemClickListener(new ClickListener(finalKeys));

                //hide the progress bar
                ProgressBar prog = (ProgressBar) activity.findViewById(R.id.teams_loading_progress);
                prog.setVisibility(View.GONE);
            }
        });

        return "";
    }

    private class ClickListener implements ListView.OnItemClickListener{

        final String[] keys;
        int pos;

        public ClickListener(String[] eventKeys){
            keys = eventKeys;
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String teamKey = keys[i];
            ViewTeam.setTeam(teamKey);
            Intent intent = new Intent(activity, ViewTeam.class);
            activity.startActivity(intent);
        }
    }
}
