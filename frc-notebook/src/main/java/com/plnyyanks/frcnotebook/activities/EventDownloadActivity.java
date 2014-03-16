package com.plnyyanks.frcnotebook.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.database.PreferenceHandler;
import com.plnyyanks.frcnotebook.tba.TBA_EventFetcher;

public class EventDownloadActivity extends Activity {

    SharedPreferences prefs;
    String currentYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(PreferenceHandler.getTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_download);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .commit();
        }

        if(prefs == null)
            prefs = PreferenceManager.getDefaultSharedPreferences(this);
        currentYear = prefs.getString("competition_season","2014");
        getActionBar().setDisplayHomeAsUpEnabled(true);
        new TBA_EventFetcher().execute(this);
    }

    @Override
    protected void onResume() {
        StartActivity.checkThemeChanged(EventDownloadActivity.class);
        super.onResume();

        if(prefs == null)
            prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if(!currentYear.equals(prefs.getString("competition_season","2014"))){
            ListView eventList = (ListView) findViewById(R.id.event_list);

            currentYear = prefs.getString("competition_season","2014");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.event_download, menu);
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
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}