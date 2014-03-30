package com.plnyyanks.frcnotebook.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.background.ShowLocalEvents;
import com.plnyyanks.frcnotebook.database.DatabaseHandler;
import com.plnyyanks.frcnotebook.database.PreferenceHandler;

public class StartActivity extends Activity implements ActionBar.OnNavigationListener {

    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    public static Context startActivityContext;
    public static Activity activity;
    public static DatabaseHandler db;
    private static int currentTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startActivityContext = this;
        activity = this;
        setTheme(PreferenceHandler.getTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        /*if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .commit();
        }*/

        getdb();
        PreferenceHandler.setAppVersion(this);

        //configure action bar to show drop down navigation
        final ActionBar bar = getActionBar();
        bar.setDisplayShowTitleEnabled(false);
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        final String[] navigationValues = getResources().getStringArray(R.array.event_list_navi_options);
        //create an adapter for the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(bar.getThemedContext(),
                android.R.layout.simple_spinner_item,android.R.id.text1,navigationValues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bar.setListNavigationCallbacks(adapter,this);

        if(savedInstanceState!=null) {
            bar.setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM,0));
        }else{
            bar.setSelectedNavigationItem(0);
        }

        new ShowLocalEvents(bar.getSelectedNavigationIndex()).execute(this);
    }

    @Override
    protected void onResume() {
        checkThemeChanged(StartActivity.class);
        super.onResume();
        new ShowLocalEvents().execute(this);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current dropdown position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current dropdown position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
                .getSelectedNavigationIndex());
    }

    public void openDownloader(View view){
        Intent intent = new Intent(this, EventDownloadActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start, menu);
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
        if(id == R.id.action_download_event){
            openDownloader(null);
        }
        return super.onOptionsItemSelected(item);
    }

    public  DatabaseHandler getdb(){
        if(db == null)
            db = new DatabaseHandler(this);

        return db;
    }

    public void closedb(){
        if(db != null)
            db.close();
    }

    public static void checkThemeChanged(Class<?> cls){
        if(currentTheme != PreferenceHandler.getTheme()){
            currentTheme = PreferenceHandler.getTheme();
            Intent intent = new Intent(startActivityContext, cls);
            startActivityContext.startActivity(intent);
        }else{
            currentTheme = PreferenceHandler.getTheme();
        }
    }

    @Override
    public boolean onNavigationItemSelected(int i, long l) {
        //show the progress bar while we load events
        ProgressBar prog = (ProgressBar) findViewById(R.id.event_list_loading_progress);
        prog.setVisibility(View.VISIBLE);
        new ShowLocalEvents(i).execute(this);
        return false;
    }
}
