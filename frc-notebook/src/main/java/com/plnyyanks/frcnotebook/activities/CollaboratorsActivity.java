package com.plnyyanks.frcnotebook.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.helpers.SocialHelper;

public class CollaboratorsActivity extends Activity {

    private static String EVENT_KEY = "eventKey";
    
    private String eventKey;
    
    public static Intent newInstance(Context context, String eventKey){
        Intent intent = new Intent(context, CollaboratorsActivity.class);
        intent.putExtra(EVENT_KEY, eventKey);
        return intent;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collaborators);
        
        if(!getIntent().hasExtra(EVENT_KEY)){
            throw new IllegalArgumentException("CollaboratorsActivity must be constructed with eventKey");
        }
        eventKey = getIntent().getStringExtra(EVENT_KEY);
        TextView keyHeader = (TextView)findViewById(R.id.eventKeyHeader);
        keyHeader.setText(eventKey);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_collaborators, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    
    public void onAddCollaborator(View v){
        EditText input = (EditText)findViewById(R.id.new_collab_email);
        String email = input.getText().toString();
        if(email.isEmpty()){
            Toast.makeText(this, "Must supply email", Toast.LENGTH_SHORT).show();
        }else {
            SocialHelper.addCollaborator(this, eventKey, email);
        }
    }
}
