package com.plnyyanks.frcnotebook.activities;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.plnyyanks.frcnotebook.Constants;
import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.datatypes.Event;
import com.plnyyanks.frcnotebook.datatypes.Match;
import com.plnyyanks.frcnotebook.datatypes.Note;

import java.util.ArrayList;
import java.util.Iterator;

public class ViewMatch extends Activity {

    private static String matchKey,eventKey,nextKey,previousKey;
    private static Event parentEvent;
    private static Match match;
    static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_match);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .commit();
        }

        context = this;
        ActionBar bar = getActionBar();
        bar.setTitle(parentEvent.getEventName()+" - "+parentEvent.getEventYear());
        bar.setSubtitle(eventKey);

        if(matchKey == null) return;

        TextView matchTitle = (TextView) findViewById(R.id.match_title);
        matchTitle.setText(match.getMatchType()+ " "+match.getMatchNumber());

        LinearLayout redList = (LinearLayout) findViewById(R.id.red_alliance);
        LinearLayout blueList = (LinearLayout) findViewById(R.id.blue_allaince);

        JsonArray   redTeams  = match.getRedAllianceTeams(),
                    blueTeams = match.getBlueAllianceTeams();

        if(redTeams.size() >0){
            redList.removeAllViews();
            Iterator<JsonElement> iterator = redTeams.iterator();
            JsonElement team;
            while(iterator.hasNext()){
                team = iterator.next();
                redList.addView(makeTextView(team.getAsString(),Constants.lparams));
            }
        }
        if(blueTeams.size() >0){
            blueList.removeAllViews();
            Iterator<JsonElement> iterator = blueTeams.iterator();
            JsonElement team;
            while(iterator.hasNext()){
                team = iterator.next();
                blueList.addView(makeTextView(team.getAsString(),Constants.lparams));
            }
        }

        if(!StartActivity.db.matchExists(nextKey)){
            Button nextButton = (Button)findViewById(R.id.next_match);
            nextButton.setVisibility(View.GONE);
        }
        if(!StartActivity.db.matchExists(previousKey)){
            Button prevButton = (Button)findViewById(R.id.prev_match);
            prevButton.setVisibility(View.GONE);
        }
    }

    private TextView makeTextView(String teamKey,LinearLayout.LayoutParams lparams){
        TextView tv;
        tv = new TextView(this);
        tv.setLayoutParams(Constants.lparams);
        tv.setTextSize(20);
        tv.setText(teamKey.substring(3));
        tv.setTag(teamKey);
        tv.setOnClickListener(new TeamClickListener(teamKey,eventKey));
        ArrayList<Note> notes = StartActivity.db.getAllNotes(teamKey,eventKey);
        if(notes.size() >0){
            tv.setTypeface(null, Typeface.BOLD);
        }
        return tv;
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
        if (id == R.id.action_settings) {
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

    class TeamClickListener implements View.OnClickListener{

        String teamKey,eventKey;

        public TeamClickListener(String teamKey, String eventKey){
            this.teamKey = teamKey;
            this.eventKey = eventKey;
        }

        @Override
        public void onClick(View view) {

            TextView noteHeader = (TextView)findViewById(R.id.team_notes);
            noteHeader.setText("Team "+teamKey.substring(3)+" Notes");
            fetchNotes();

        }

        private void fetchNotes(){
            ArrayList<Note> notes = StartActivity.db.getAllNotes(teamKey,eventKey);
            LinearLayout notesList = (LinearLayout)findViewById(R.id.team_notes_list);
            notesList.removeAllViews();
            if(notes.size() == 0){
                TextView t = new TextView(context);
                t.setLayoutParams(Constants.lparams);
                t.setText("No Notes for This Team");
                notesList.addView(t);
            }else{
                for(Note n:notes){
                    TextView t = new TextView(context);
                    t.setLayoutParams(Constants.lparams);
                    t.setText("• " + n.getNote());
                    t.setTextSize(18);
                    notesList.addView(t);
                }
            }
            Button addNote = new Button(context);
            addNote.setText("Add Note");
            addNote.setLayoutParams(Constants.lparams);
            addNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final EditText noteEditField = new EditText(context);
                    noteEditField.setText("");
                    noteEditField.setHint("Enter your note");
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Note on Team " + teamKey.substring(3));
                    builder.setView(noteEditField);
                    builder.setPositiveButton("Add",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    String resultText;
                                    Note note = new Note(eventKey,matchKey,teamKey,noteEditField.getText().toString());
                                    if(StartActivity.db.addNote(note) != -1){
                                        resultText = "Note added sucessfully";
                                        fetchNotes();
                                    }else{
                                        resultText = "Error adding note to database";
                                    }
                                    Toast.makeText(context, resultText, Toast.LENGTH_SHORT).show();
                                    dialog.cancel();
                                }
                            });

                    builder.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    builder.create().show();

                }
            });
            addView(addNote);
        }

        private void addView(View view){
            LinearLayout notesList = (LinearLayout)findViewById(R.id.team_notes_list);
            notesList.addView(view);
        }
    }

}