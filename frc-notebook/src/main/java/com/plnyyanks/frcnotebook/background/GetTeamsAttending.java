package com.plnyyanks.frcnotebook.background;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.activities.StartActivity;
import com.plnyyanks.frcnotebook.activities.ViewTeam;
import com.plnyyanks.frcnotebook.adapters.ListViewArrayAdapter;
import com.plnyyanks.frcnotebook.datatypes.ListElement;
import com.plnyyanks.frcnotebook.datatypes.ListItem;
import com.plnyyanks.frcnotebook.datatypes.Team;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by phil on 2/23/14.
 */
public class GetTeamsAttending extends AsyncTask<String,String,String> {

    private Activity activity;
    private String msg = "";

    public GetTeamsAttending(Activity activity){
        super();
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... strings) {
        String eventKey = strings[0];

        ArrayList<Team> teamList = StartActivity.db.getAllTeamAtEvent(eventKey);
        Collections.sort(teamList);
        final ArrayList<ListItem> teams = new ArrayList<ListItem>();
        final ArrayList<String> keys = new ArrayList<String>();

        for (Team t : teamList) {
            teams.add(new ListElement(Integer.toString(t.getTeamNumber()), t.getTeamKey()));
            keys.add(t.getTeamKey());
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListViewArrayAdapter adapter = new ListViewArrayAdapter(activity,teams,keys);
                ListView teamListView = (ListView) activity.findViewById(R.id.team_list);
                if(teamListView != null){
                    teamListView.setAdapter(adapter);
                    teamListView.setOnItemClickListener(new ClickListener(keys));

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
}
