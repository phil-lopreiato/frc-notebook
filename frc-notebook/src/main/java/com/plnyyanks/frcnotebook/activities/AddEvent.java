package com.plnyyanks.frcnotebook.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.plnyyanks.frcnotebook.Constants;
import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.background.ValidateNewEventData;
import com.plnyyanks.frcnotebook.database.PreferenceHandler;
import com.plnyyanks.frcnotebook.dialogs.DatePickerFragment;

/**
 * File created by phil on 3/1/14.
 * Copyright 2015, Phil Lopreiato
 * This file is part of FRC Notebook
 * FRC Notebook is licensed under the MIT License 
 * (http://opensource.org/licenses/MIT)
 */
public class AddEvent extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(PreferenceHandler.getTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
    }

    @Override
    protected void onResume() {
        StartActivity.checkThemeChanged(AddEvent.class);
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return false;
            case R.id.action_create_evnet:
                Log.d(Constants.LOG_TAG,"adding event");
                new ValidateNewEventData(this).execute("");
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showDatePicker(View v){
        new DatePickerFragment(v).show(getFragmentManager(),"datePicker");
    }
}
