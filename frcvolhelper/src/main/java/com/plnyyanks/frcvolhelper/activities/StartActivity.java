package com.plnyyanks.frcvolhelper.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.plnyyanks.frcvolhelper.R;
import com.plnyyanks.frcvolhelper.database.DatabaseHandler;
import com.plnyyanks.frcvolhelper.datatypes.Event;
import com.plnyyanks.frcvolhelper.tba.TBA_API;

import java.util.List;

public class StartActivity extends Activity {

    public static Context startActivityContext;
    public static DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .commit();
        }

        startActivityContext = this;
        getdb();

        showEventsFromDatabase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showEventsFromDatabase();
    }

    private void showEventsFromDatabase(){
        getdb();
        List<Event> storedEvents = db.getAllEvents();

        LinearLayout eventList = (LinearLayout) findViewById(R.id.event_list);
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        eventList.removeAllViews();
        for(Event e:storedEvents){
            TextView tv=new TextView(this);
            tv.setLayoutParams(lparams);
            tv.setText("• " + e.getEventName());
            tv.setTextSize(20);
            eventList.addView(tv);
        }

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
            return true;
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

}
