package com.plnyyanks.frcnotebook.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.background.ShowLocalEvents;
import com.plnyyanks.frcnotebook.database.DatabaseHandler;
import com.plnyyanks.frcnotebook.database.PreferenceHandler;

public class StartActivity extends Activity{

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
        new ShowLocalEvents().execute(this);
    }

    @Override
    protected void onResume() {
        checkThemeChanged(StartActivity.class);
        super.onResume();
        new ShowLocalEvents().execute(this);
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
}
