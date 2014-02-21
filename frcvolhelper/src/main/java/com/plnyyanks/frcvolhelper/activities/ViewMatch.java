package com.plnyyanks.frcvolhelper.activities;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.plnyyanks.frcvolhelper.R;
import com.plnyyanks.frcvolhelper.datatypes.Event;
import com.plnyyanks.frcvolhelper.datatypes.Match;

import java.util.Iterator;

public class ViewMatch extends Activity {

    private static String matchKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_match);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .commit();
        }

        Match match = StartActivity.db.getMatch(matchKey);
        Event parentEvent = match.getParentEvent();

        ActionBar bar = getActionBar();
        bar.setTitle(parentEvent.getEventName()+" - "+parentEvent.getEventYear());
        bar.setSubtitle(match.getMatchKey());

        if(matchKey == null) return;

        TextView matchTitle = (TextView) findViewById(R.id.match_title);
        matchTitle.setText(match.getMatchType()+ " "+match.getMatchNumber());

        LinearLayout redList = (LinearLayout) findViewById(R.id.red_alliance);
        LinearLayout blueList = (LinearLayout) findViewById(R.id.blue_allaince);
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        JsonArray   redTeams  = match.getRedAllianceTeams(),
                    blueTeams = match.getBlueAllianceTeams();

        if(redTeams.size() >0){
            redList.removeAllViews();
            Iterator<JsonElement> iterator = redTeams.iterator();
            JsonElement team;
            while(iterator.hasNext()){
                team = iterator.next();
                redList.addView(makeTextView(team.getAsString(),lparams));
            }
        }
        if(blueTeams.size() >0){
            blueList.removeAllViews();
            Iterator<JsonElement> iterator = redTeams.iterator();
            JsonElement team;
            while(iterator.hasNext()){
                team = iterator.next();
                blueList.addView(makeTextView(team.getAsString(),lparams));
            }
        }
    }

    private TextView makeTextView(String teamKey,LinearLayout.LayoutParams lparams){
        TextView tv;
        tv = new TextView(this);
        tv.setLayoutParams(lparams);
        tv.setTextSize(20);
        tv.setText(teamKey.substring(3));
        tv.setTag(teamKey);
        return tv;
    }

    public static void setMatchKey(String key){
        matchKey = key;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_match, menu);
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

}