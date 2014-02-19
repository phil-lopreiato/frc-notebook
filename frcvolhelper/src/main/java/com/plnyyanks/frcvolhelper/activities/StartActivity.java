package com.plnyyanks.frcvolhelper.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.plnyyanks.frcvolhelper.R;
import com.plnyyanks.frcvolhelper.tba.TBA_API;

public class StartActivity extends Activity {

    public static Context startActivityContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .commit();
        }

        startActivityContext = this;

        LinearLayout eventList = (LinearLayout) findViewById(R.id.event_list);
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
       /* TextView tv=new TextView(this);
        tv.setLayoutParams(lparams);
        tv.setText("moo!");
        eventList.addView(tv); */

        TBA_API.getEventsForSeason("2014");

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



}
