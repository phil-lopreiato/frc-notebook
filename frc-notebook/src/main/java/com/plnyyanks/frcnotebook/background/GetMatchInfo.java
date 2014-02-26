package com.plnyyanks.frcnotebook.background;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.plnyyanks.frcnotebook.Constants;
import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.activities.StartActivity;
import com.plnyyanks.frcnotebook.datatypes.Match;
import com.plnyyanks.frcnotebook.datatypes.Note;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by phil on 2/23/14.
 */
public class GetMatchInfo extends AsyncTask<String,String,String> {


    private Activity activity;
    String    previousMatchKey,
              thisMatchKey,
              nextMatchKey,
              eventKey;

    public GetMatchInfo(Activity activity) {
        this.activity = activity;
    }
    @Override
    protected String doInBackground(String... strings) {
        previousMatchKey = strings[0];
        thisMatchKey     = strings[1];
        nextMatchKey     = strings[2];
        eventKey         = strings[3];

        Match match = StartActivity.db.getMatch(thisMatchKey);
        TextView matchTitle = (TextView) activity.findViewById(R.id.match_title);
        String titleString = match.getMatchType()+(match.getMatchType().equals("Quals")?" ":(" "+match.getSetNumber()+ " Match "))+match.getMatchNumber();
        matchTitle.setText(titleString);


        TextView redHeader = (TextView)activity.findViewById(R.id.red_score);
        redHeader.setText(Integer.toString(match.getRedScore())+ " Points");

        TextView blueHeader = (TextView)activity.findViewById(R.id.blue_score);
        blueHeader.setText(Integer.toString(match.getBlueScore())+" Points");

        LinearLayout redList = (LinearLayout) activity.findViewById(R.id.red_alliance);
        LinearLayout blueList = (LinearLayout) activity.findViewById(R.id.blue_allaince);

        JsonArray redTeams  = match.getRedAllianceTeams(),
                blueTeams = match.getBlueAllianceTeams();

        if(redTeams.size() >0){
            redList.removeAllViews();
            Iterator<JsonElement> iterator = redTeams.iterator();
            JsonElement team;
            while(iterator.hasNext()){
                team = iterator.next();
                redList.addView(makeTextView(team.getAsString(), Constants.lparams));
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

        if(!StartActivity.db.matchExists(nextMatchKey)){
            Button nextButton = (Button)activity.findViewById(R.id.next_match);
            nextButton.setVisibility(View.GONE);
        }
        if(!StartActivity.db.matchExists(previousMatchKey)){
            Button prevButton = (Button)activity.findViewById(R.id.prev_match);
            prevButton.setVisibility(View.GONE);
        }
        return null;
    }

    private TextView makeTextView(String teamKey,LinearLayout.LayoutParams lparams){
        TextView tv;
        tv = new TextView(activity);
        tv.setLayoutParams(Constants.lparams);
        tv.setTextSize(25);
        tv.setText(teamKey.substring(3));
        tv.setTag(teamKey);
        tv.setOnClickListener(new TeamClickListener(teamKey,eventKey));
        ArrayList<Note> notes = StartActivity.db.getAllNotes(teamKey,eventKey);
        if(notes.size() >0){
            tv.setTypeface(null, Typeface.BOLD);
        }
        return tv;
    }

    class TeamClickListener implements View.OnClickListener{

        String teamKey,eventKey;

        public TeamClickListener(String teamKey, String eventKey){
            this.teamKey = teamKey;
            this.eventKey = eventKey;
        }

        @Override
        public void onClick(View view) {

            TextView noteHeader = (TextView)activity.findViewById(R.id.team_notes);
            noteHeader.setText("Team "+teamKey.substring(3)+" Notes");
            fetchNotes();

        }

        private void fetchNotes(){
            ArrayList<Note> notes = StartActivity.db.getAllNotes(teamKey,eventKey);
            LinearLayout notesList = (LinearLayout)activity.findViewById(R.id.team_notes_list);
            notesList.removeAllViews();
            if(notes.size() == 0){
                TextView t = new TextView(activity);
                t.setLayoutParams(Constants.lparams);
                t.setText("No Notes for This Team");
                notesList.addView(t);
            }else{
                for(Note n:notes){
                    TextView t = new TextView(activity);
                    t.setLayoutParams(Constants.lparams);
                    t.setText("• " + n.getNote());
                    t.setTextSize(18);
                    notesList.addView(t);
                }
            }
            Button addNote = (Button)activity.findViewById(R.id.add_note_button);
            addNote.setVisibility(View.VISIBLE);
            addNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final EditText noteEditField = new EditText(activity);
                    noteEditField.setText("");
                    noteEditField.setHint("Enter your note");
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle("Note on Team " + teamKey.substring(3));
                    builder.setView(noteEditField);
                    builder.setPositiveButton("Add",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    String resultText;
                                    Note note = new Note(eventKey,thisMatchKey,teamKey,noteEditField.getText().toString());
                                    if(StartActivity.db.addNote(note) != -1){
                                        resultText = "Note added sucessfully";
                                        fetchNotes();
                                    }else{
                                        resultText = "Error adding note to database";
                                    }
                                    Toast.makeText(activity, resultText, Toast.LENGTH_SHORT).show();
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
        }

        private void addView(View view){
            LinearLayout notesList = (LinearLayout)activity.findViewById(R.id.team_notes_list);
            notesList.addView(view);
        }
    }
}
