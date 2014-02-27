package com.plnyyanks.frcnotebook.background;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.plnyyanks.frcnotebook.Constants;
import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.activities.StartActivity;
import com.plnyyanks.frcnotebook.adapters.ActionBarCallback;
import com.plnyyanks.frcnotebook.adapters.EventListArrayAdapter;
import com.plnyyanks.frcnotebook.datatypes.Event;
import com.plnyyanks.frcnotebook.datatypes.Match;
import com.plnyyanks.frcnotebook.datatypes.Note;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by phil on 2/23/14.
 */
public class GetMatchInfo extends AsyncTask<String,String,String> {


    private static Activity activity;
    private static String    previousMatchKey,
                             thisMatchKey,
                             nextMatchKey,
                             eventKey,
                             selectedTeam;
    private static EventListArrayAdapter adapter;
    private static Object mActionMode;
    private static ListView noteListView;
    private static int selectedNote;

    public GetMatchInfo(Activity activity) {
        this.activity = activity;
    }
    @Override
    protected String doInBackground(String... strings) {
        previousMatchKey = strings[0];
        thisMatchKey     = strings[1];
        nextMatchKey     = strings[2];
        eventKey         = strings[3];
        selectedNote     = -1;
        selectedTeam     = null;

        Match match = StartActivity.db.getMatch(thisMatchKey);
        TextView matchTitle = (TextView) activity.findViewById(R.id.match_title);
        String titleString = match.getMatchType()+(match.getMatchType().equals("Quals")?" ":(" "+match.getSetNumber()+ " Match "))+match.getMatchNumber();
        matchTitle.setText(titleString);


        TextView redHeader = (TextView)activity.findViewById(R.id.red_score);
        redHeader.setText(Integer.toString(match.getRedScore())+ " Points");

        TextView blueHeader = (TextView)activity.findViewById(R.id.blue_score);
        blueHeader.setText(Integer.toString(match.getBlueScore())+" Points");

        final LinearLayout redList = (LinearLayout) activity.findViewById(R.id.red_alliance);
        final LinearLayout blueList = (LinearLayout) activity.findViewById(R.id.blue_allaince);

        JsonArray redTeams  = match.getRedAllianceTeams(),
                blueTeams = match.getBlueAllianceTeams();

        if(redTeams.size() >0){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    redList.removeAllViews();
                }
            });
            Iterator<JsonElement> iterator = redTeams.iterator();
            JsonElement team;
            while(iterator.hasNext()){
                team = iterator.next();
                final TextView v = makeTextView(team.getAsString(), Constants.lparams);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        redList.addView(v);
                    }
                });
            }
        }
        if(blueTeams.size() >0){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    blueList.removeAllViews();
                }
            });
            Iterator<JsonElement> iterator = blueTeams.iterator();
            JsonElement team;
            while(iterator.hasNext()){
                team = iterator.next();
                final TextView v = makeTextView(team.getAsString(),Constants.lparams);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        blueList.addView(v);
                    }
                });

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
        tv.setOnClickListener(new TeamClickListener(teamKey));
        ArrayList<Note> notes = StartActivity.db.getAllNotes(teamKey,eventKey);
        if(notes.size() >0){
            tv.setTypeface(null, Typeface.BOLD);
        }
        return tv;
    }

    class TeamClickListener implements View.OnClickListener{

        String teamKey;

        public TeamClickListener(String teamKey){
            this.teamKey = teamKey;
        }

        @Override
        public void onClick(View view) {

            TextView noteHeader = (TextView)activity.findViewById(R.id.team_notes);
            noteHeader.setText("Team "+teamKey.substring(3)+" Notes");
            selectedTeam = teamKey;
            fetchNotes(teamKey);

        }
    }

    private void fetchNotes(String teamKey){
        ArrayList<Note> noteList = StartActivity.db.getAllNotes(teamKey,eventKey);
        noteListView = (ListView) activity.findViewById(R.id.team_notes_list);
        noteListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        String[] notes, ids;
        Note n;
        if(noteList.size() == 0){
            notes = new String[1];
            notes[0] = activity.getString(R.string.no_team_notes);

            ids = new String[1];
            ids[0] = "-1";
        }else{
            notes = new String[noteList.size()];
            ids   = new String[noteList.size()];
            for(int i=0;i<noteList.size();i++){
                n = noteList.get(i);
                notes[i] = Note.buildMatchNoteTitle(n,false, true);
                ids[i] = Short.toString(n.getId());
            }
        }

        final String[] finalNotes = notes,
                finalIds  = ids;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter = new EventListArrayAdapter(activity,finalNotes,finalIds);
                noteListView.setAdapter(adapter);
                noteListView.setOnItemClickListener(new NoteClickListener());
                noteListView.setOnItemLongClickListener(new LongClickListener());
            }
        });

        TextView nothingSelected = (TextView)activity.findViewById(R.id.no_team_selected);
        nothingSelected.setVisibility(View.GONE);

    }

    public static void addMatchNote(){
        if(selectedTeam==null){
            //no team selected, can't add note
            Toast.makeText(activity,"Error: no team selected",Toast.LENGTH_SHORT).show();
            Log.w(Constants.LOG_TAG,"Can't create a note with no team selected");
            return;
        }

        final EditText noteEditField = new EditText(activity);
        noteEditField.setText("");
        noteEditField.setHint("Enter your note");
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Note on Team " + selectedTeam.substring(3));
        builder.setView(noteEditField);
        builder.setPositiveButton("Add",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String resultText;
                        Note note = new Note(eventKey,thisMatchKey,selectedTeam,noteEditField.getText().toString());
                        short newNoteId = StartActivity.db.addNote(note);
                        if(newNoteId != -1){
                            resultText = "Note added sucessfully";
                            adapter.values.add(Note.buildMatchNoteTitle(note,false,true));
                            adapter.keys.add(Short.toString(newNoteId));
                            adapter.notifyDataSetChanged();
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

    private class NoteClickListener implements AdapterView.OnItemClickListener {

        public NoteClickListener() {
            super();
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Log.d(Constants.LOG_TAG,"Note clicked at position: "+i+" (selected: "+selectedNote+")");
            if(selectedNote==-1){
                final Note oldNote = StartActivity.db.getNote(Short.parseShort(adapter.keys.get(i)));
                final EditText noteEditField = new EditText(activity);
                //noteEditField.setId(999);
                noteEditField.setText(oldNote.getNote());

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Note on Team " + GetNotesForTeam.getTeamNumber());
                builder.setView(noteEditField);
                builder.setMessage("Edit your note.");
                builder.setPositiveButton("Update",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                oldNote.setNote(noteEditField.getText().toString());
                                StartActivity.db.updateNote(oldNote);
                                updateNoteInList(oldNote);
                                adapter.notifyDataSetChanged();
                                dialog.cancel();
                            }
                        });

                builder.setNeutralButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                builder.create().show();
            }
        }

        private void updateNoteInList(Note newNote) {
            int index = Arrays.asList(adapter.keys).indexOf(Short.toString(newNote.getId()));
            if(index == -1){
                //not found. quit
                return;
            }else{
                adapter.values.set(index, newNote.getNote());
            }
        }
    }

    private class LongClickListener implements ListView.OnItemLongClickListener{

        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            Log.d(Constants.LOG_TAG, "Note Long Click: " + i);
            //eventList.setOnItemClickListener(null);
            view.setSelected(true);
            adapter.notifyDataSetChanged();
            selectedNote = i;
            // start the CAB using the ActionMode.Callback defined above
            mActionMode = activity.startActionMode(mActionModeCallback);
            return false;
        }
    }

    private ActionMode.Callback mActionModeCallback = new ActionBarCallback() {
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    confirmAndDelete(selectedNote);
                    // the Action was executed, close the CAB
                    selectedNote = -1;
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            Log.d(Constants.LOG_TAG, "Destroy CAB");
            mActionMode = null;
            noteListView.requestFocusFromTouch();
            noteListView.clearChoices();
            adapter.notifyDataSetChanged();
            selectedNote = -1;
        }

        private void confirmAndDelete(final int notePosition){
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Confirm Deletion");
            builder.setMessage("Are you sure you want to delete this note?");
            builder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //delete the event now
                            StartActivity.db.deleteNote(adapter.getEventKey(notePosition));
                            Toast.makeText(activity, "Deleted note from database", Toast.LENGTH_SHORT).show();
                            adapter.removeAt(notePosition);
                            adapter.notifyDataSetChanged();
                            dialog.cancel();
                        }
                    });

            builder.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            builder.create().show();
        }
    };
}
