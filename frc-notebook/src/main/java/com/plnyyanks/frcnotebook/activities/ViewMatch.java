package com.plnyyanks.frcnotebook.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.plnyyanks.frcnotebook.Constants;
import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.background.GetNotesForMatch;
import com.plnyyanks.frcnotebook.database.PreferenceHandler;
import com.plnyyanks.frcnotebook.datafeed.MatchDetailFetcher;
import com.plnyyanks.frcnotebook.datatypes.Event;
import com.plnyyanks.frcnotebook.datatypes.Match;
import com.plnyyanks.frcnotebook.dialogs.AddNoteDialog;

/**
 * File created by phil on 3/1/14.
 * Copyright 2014, Phil Lopreiato
 * This file is part of FRC Notebook.
 * FRC Notebook is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * FRC Notebook is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with FRC Notebook. If not, see http://www.gnu.org/licenses/.
 */
public class ViewMatch extends Activity {

    public static String matchKey,eventKey,nextKey,previousKey;
    private static Event parentEvent;
    private static Match match;
    static Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(PreferenceHandler.getTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_match);

        activity = this;
        ActionBar bar = getActionBar();
        bar.setTitle(parentEvent.getEventName()+" - "+parentEvent.getEventYear());
        bar.setSubtitle(eventKey);
        bar.setDisplayHomeAsUpEnabled(true);

        if(matchKey == null) return;

        if(PreferenceHandler.getTheme()==R.style.theme_dark){
            ImageView nextMatch = (ImageView)findViewById(R.id.next_match);
            nextMatch.setBackgroundResource(R.drawable.ic_action_next_item_dark);
            ImageView prevMatch = (ImageView)findViewById(R.id.prev_match);
            prevMatch.setBackgroundResource(R.drawable.ic_action_previous_item_dark);
        }

        new GetNotesForMatch(this).execute(previousKey, matchKey, nextKey, eventKey);

        if(PreferenceHandler.getFMEnabled()) {
            TextView fieldMonitor = (TextView) findViewById(R.id.field_monitor_link);
            fieldMonitor.setVisibility(View.VISIBLE);
            fieldMonitor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, FieldMonitorActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        StartActivity.checkThemeChanged(ViewMatch.class);
        super.onResume();
    }

    public static void setMatchKey(String key){
        matchKey = key;
        match = StartActivity.db.getMatch(matchKey);
        parentEvent = match.getParentEvent();
        eventKey = parentEvent.getEventKey();

        nextKey = match.getNextMatch();
        previousKey = match.getPreviousMatch();
        Log.d(Constants.LOG_TAG,"Set View Match Vars, matchKey:"+matchKey+", eventKey:"+eventKey+", next: "+nextKey+", prev: "+previousKey);
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
        switch(id) {
            case (R.id.action_settings):
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_update_match:
                Toast.makeText(this, "Updating data for " + matchKey, Toast.LENGTH_SHORT).show();
                new MatchDetailFetcher(activity, eventKey).execute(new String[]{"[\"" + matchKey + "\"]", eventKey});
                return true;

            case R.id.action_add_note:
                new AddNoteDialog(match,GetNotesForMatch.getRedAdaper(),
                                        GetNotesForMatch.getBlueAdapter(),
                                        GetNotesForMatch.getGenericAdapter())
                     .show(getFragmentManager(), "Add Note");
                return true;

            case R.id.action_view_tba:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://thebluealliance.com/match/"+matchKey)));
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void previousMatch(View view) {
        setMatchKey(previousKey);
        Intent intent = new Intent(this, ViewMatch.class);
        startActivity(intent);
    }
    public void nextMatch(View view) {
        setMatchKey(nextKey);
        Intent intent = new Intent(this, ViewMatch.class);
        startActivity(intent);
    }
}