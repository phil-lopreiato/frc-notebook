package com.plnyyanks.frcnotebook.background;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;
import android.view.ActionMode;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.plnyyanks.frcnotebook.Constants;
import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.activities.StartActivity;
import com.plnyyanks.frcnotebook.adapters.ActionBarCallback;
import com.plnyyanks.frcnotebook.adapters.AllianceExpandableListAdapter;
import com.plnyyanks.frcnotebook.adapters.MatchListExpandableListAdapter;
import com.plnyyanks.frcnotebook.datatypes.ListElement;
import com.plnyyanks.frcnotebook.datatypes.ListGroup;
import com.plnyyanks.frcnotebook.datatypes.Match;
import com.plnyyanks.frcnotebook.datatypes.Note;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by phil on 3/10/14.
 */
public class GetNotesForMatch extends AsyncTask<String, String, String> {

    private static Activity activity;
    private static String previousMatchKey,
            thisMatchKey,
            nextMatchKey,
            eventKey;
    private static SparseArray<ListGroup> redGroups = new SparseArray<ListGroup>(),
            blueGroups = new SparseArray<ListGroup>();
    public static Object mActionMode;
    private static ListView noteListView;
    public static String selectedNote="";
    private static AllianceExpandableListAdapter redAdaper, blueAdapter;
    private static ExpandableListView redAlliance,blueAlliance;

    public GetNotesForMatch(Activity activity) {
        super();
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... strings) {
        previousMatchKey = strings[0];
        thisMatchKey = strings[1];
        nextMatchKey = strings[2];
        eventKey = strings[3];
        selectedNote = "";

        Match match = StartActivity.db.getMatch(thisMatchKey);
        TextView matchTitle = (TextView) activity.findViewById(R.id.match_title);
        String titleString = match.getMatchType() + (match.getMatchType().equals("Quals") ? " " : (" " + match.getSetNumber() + " Match ")) + match.getMatchNumber();
        matchTitle.setText(titleString);

        TextView redHeader = (TextView) activity.findViewById(R.id.red_score);
        if (match.getRedScore() >= 0) {
            redHeader.setText(Integer.toString(match.getRedScore()) + " Points");
        } else {
            redHeader.setVisibility(View.GONE);
        }

        TextView blueHeader = (TextView) activity.findViewById(R.id.blue_score);
        if (match.getBlueScore() >= 0) {
            blueHeader.setText(Integer.toString(match.getBlueScore()) + " Points");
        } else {
            blueHeader.setVisibility(View.GONE);
        }

        JsonArray redTeams = match.getRedAllianceTeams(),
                blueTeams = match.getBlueAllianceTeams();

        if (redTeams.size() > 0) {
            Iterator<JsonElement> iterator = redTeams.iterator();
            String teamKey;
            for (int i = 0; iterator.hasNext(); i++) {
                teamKey = iterator.next().getAsString();
                ArrayList<Note> notes = StartActivity.db.getAllNotes(teamKey, eventKey, thisMatchKey);
                int size = notes.size();
                ListGroup teamHeader = new ListGroup(teamKey.substring(3)+(size>0?(" ("+ notes.size()+")"):""));
                teamHeader.children.add("Add note");
                teamHeader.children_keys.add("-1");

                for (Note n : notes) {
                    teamHeader.children.add(Note.buildMatchNoteTitle(n, false, true,true));
                    teamHeader.children_keys.add(Short.toString(n.getId()));
                }

                redGroups.append(i, teamHeader);
            }
        }
        if (blueTeams.size() > 0) {
            Iterator<JsonElement> iterator = blueTeams.iterator();
            String teamKey;
            for (int i = 0; iterator.hasNext(); i++) {
                teamKey = iterator.next().getAsString();
                ArrayList<Note> notes = StartActivity.db.getAllNotes(teamKey, eventKey, thisMatchKey);
                int size = notes.size();
                ListGroup teamHeader = new ListGroup(teamKey.substring(3)+(size>0?(" ("+ notes.size()+")"):""));
                teamHeader.children.add("Add note");
                teamHeader.children_keys.add("-1");

                for (Note n : notes) {
                    teamHeader.children.add(Note.buildMatchNoteTitle(n, false, true,true));
                    teamHeader.children_keys.add(Short.toString(n.getId()));
                }

                blueGroups.append(i, teamHeader);
            }
        }

        if (!StartActivity.db.matchExists(nextMatchKey)) {
            ImageView nextButton = (ImageView) activity.findViewById(R.id.next_match);
            nextButton.setVisibility(View.GONE);
        }
        if (!StartActivity.db.matchExists(previousMatchKey)) {
            ImageView prevButton = (ImageView) activity.findViewById(R.id.prev_match);
            prevButton.setVisibility(View.GONE);
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                redAlliance = (ExpandableListView) activity.findViewById(R.id.red_teams);
                if (redAlliance == null)
                    return;
                redAdaper = new AllianceExpandableListAdapter(activity, redGroups);
                redAlliance.setAdapter(redAdaper);

                blueAlliance = (ExpandableListView) activity.findViewById(R.id.blue_teams);
                if (redAlliance == null)
                    return;
                blueAdapter = new AllianceExpandableListAdapter(activity, blueGroups);
                blueAlliance.setAdapter(blueAdapter);

                //hide the progress bar
                ProgressBar prog = (ProgressBar) activity.findViewById(R.id.match_loading_progress);
                prog.setVisibility(View.GONE);
            }
        });

        return null;
    }

    public static void updateListData(){
        redAdaper.notifyDataSetChanged();
        blueAdapter.notifyDataSetChanged();
    }

    public static ActionMode.Callback mActionModeCallback = new ActionBarCallback() {
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    confirmAndDelete(selectedNote);
                    // the Action was executed, close the CAB
                    selectedNote = "";
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
            redAlliance.requestFocusFromTouch();
            redAlliance.clearChoices();
            blueAlliance.clearChoices();
            updateListData();
        }

        private void confirmAndDelete(final String noteId){
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Confirm Deletion");
            builder.setMessage("Are you sure you want to delete this note?");
            builder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //delete the event now
                            StartActivity.db.deleteNote(noteId);
                            redAdaper.removeNote(Short.parseShort(noteId));
                            blueAdapter.removeNote(Short.parseShort(noteId));
                            updateListData();
                            Toast.makeText(activity, "Deleted note from database", Toast.LENGTH_SHORT).show();
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
