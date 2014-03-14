package com.plnyyanks.frcnotebook.activities;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.plnyyanks.frcnotebook.Constants;
import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.background.GetMatchInfo;
import com.plnyyanks.frcnotebook.background.GetNotesForMatch;
import com.plnyyanks.frcnotebook.database.PreferenceHandler;
import com.plnyyanks.frcnotebook.datatypes.Event;
import com.plnyyanks.frcnotebook.datatypes.Match;
import com.plnyyanks.frcnotebook.tba.TBA_EventDetailFetcher;
import com.plnyyanks.frcnotebook.tba.TBA_MatchDetailFetcher;

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

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .commit();
        }

        activity = this;
        ActionBar bar = getActionBar();
        bar.setTitle(parentEvent.getEventName()+" - "+parentEvent.getEventYear());
        bar.setSubtitle(eventKey);

        if(matchKey == null) return;

        if(PreferenceHandler.getTheme()==R.style.theme_dark){
            ImageView nextMatch = (ImageView)findViewById(R.id.next_match);
            nextMatch.setBackgroundResource(R.drawable.ic_action_next_item_dark);
            ImageView prevMatch = (ImageView)findViewById(R.id.prev_match);
            prevMatch.setBackgroundResource(R.drawable.ic_action_previous_item_dark);
        }

        new GetNotesForMatch(this).execute(previousKey, matchKey, nextKey, eventKey);
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

        nextKey = matchKey.replaceFirst("\\d+$",Integer.toString(match.getMatchNumber() + 1));
        previousKey = matchKey.replaceFirst("\\d+$",Integer.toString(match.getMatchNumber()-1));
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
                new TBA_MatchDetailFetcher(activity, eventKey).execute(new String[]{"[\"" + matchKey + "\"]", eventKey});
                return true;

            case R.id.action_add_note:
                new AddNoteDialog(match).show(getFragmentManager(), "Add Note");
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