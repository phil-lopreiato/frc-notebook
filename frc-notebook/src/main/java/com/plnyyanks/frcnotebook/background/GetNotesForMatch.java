package com.plnyyanks.frcnotebook.background;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;
import android.view.ActionMode;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import com.plnyyanks.frcnotebook.adapters.ListViewArrayAdapter;
import com.plnyyanks.frcnotebook.database.PreferenceHandler;
import com.plnyyanks.frcnotebook.datatypes.ListElement;
import com.plnyyanks.frcnotebook.datatypes.ListGroup;
import com.plnyyanks.frcnotebook.datatypes.ListItem;
import com.plnyyanks.frcnotebook.datatypes.Match;
import com.plnyyanks.frcnotebook.datatypes.Note;
import com.plnyyanks.frcnotebook.dialogs.DeleteDialog;
import com.plnyyanks.frcnotebook.dialogs.EditNoteDialog;

import java.util.ArrayList;
import java.util.Iterator;

public class GetNotesForMatch extends AsyncTask<String, String, String> {

    private static Activity activity;
    private static String previousMatchKey,
                          nextMatchKey;
    private static SparseArray<ListGroup> redGroups = new SparseArray<ListGroup>(),
                                          blueGroups = new SparseArray<ListGroup>();
    public static Object mActionMode;
    private static ListView genericList;
    public static String selectedNote="";
    private static AllianceExpandableListAdapter redAdaper, blueAdapter;
    private static ExpandableListView redAlliance,blueAlliance;
    private static  ListViewArrayAdapter genericAdapter;

    public GetNotesForMatch(Activity activity) {
        super();
        GetNotesForMatch.activity = activity;
    }

    @Override
    protected String doInBackground(String... strings) {
        String thisMatchKey,eventKey;
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
        if (match.getRedScore() >= 0 && PreferenceHandler.showMatchScores()) {
            redHeader.setText(Integer.toString(match.getRedScore()) + " Points");
        } else {
            redHeader.setVisibility(View.GONE);
        }

        TextView blueHeader = (TextView) activity.findViewById(R.id.blue_score);
        if (match.getBlueScore() >= 0 && PreferenceHandler.showMatchScores()) {
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
                ArrayList<Note> notes = new ArrayList<Note>();
                if(PreferenceHandler.showGeneralNotes()){
                    notes.addAll(StartActivity.db.getAllNotes(teamKey,"all","all"));
                    notes.addAll(StartActivity.db.getAllNotes(teamKey,eventKey,"all"));
                }
                notes.addAll(StartActivity.db.getAllNotes(teamKey, eventKey, thisMatchKey));
                int size = notes.size();
                ListGroup teamHeader = new ListGroup(teamKey.substring(3)+(size>0?(" ("+ notes.size()+")"):""));

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
                ArrayList<Note> notes = new ArrayList<Note>();
                if(PreferenceHandler.showGeneralNotes()){
                    notes.addAll(StartActivity.db.getAllNotes(teamKey,"all","all"));
                    notes.addAll(StartActivity.db.getAllNotes(teamKey,eventKey,"all"));
                }
                notes.addAll(StartActivity.db.getAllNotes(teamKey, eventKey, thisMatchKey));
                int size = notes.size();
                ListGroup teamHeader = new ListGroup(teamKey.substring(3)+(size>0?(" ("+ notes.size()+")"):""));


                for (Note n : notes) {
                    teamHeader.children.add(Note.buildMatchNoteTitle(n, false, true,true));
                    teamHeader.children_keys.add(Short.toString(n.getId()));
                }

                blueGroups.append(i, teamHeader);
            }
        }

        //generic notes go here
        final ArrayList<Note> genericNotes = StartActivity.db.getAllNotes("all",eventKey,match.getMatchKey());
        Log.d(Constants.LOG_TAG,"Found "+genericNotes.size()+" generic notes");
        ArrayList<ListItem> genericVals = new ArrayList<ListItem>();
        ArrayList<String> genericKeys = new ArrayList<String>();
        for(Note n:genericNotes){
            genericVals.add(new ListElement(n.getNote(),Short.toString(n.getId())));
            genericKeys.add(Short.toString(n.getId()));
        }
        genericAdapter = new ListViewArrayAdapter(activity,genericVals,genericKeys);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!StartActivity.db.matchExists(nextMatchKey)) {
                    ImageView nextButton = (ImageView) activity.findViewById(R.id.next_match);
                    nextButton.setVisibility(View.GONE);
                }
                if (!StartActivity.db.matchExists(previousMatchKey)) {
                    ImageView prevButton = (ImageView) activity.findViewById(R.id.prev_match);
                    prevButton.setVisibility(View.GONE);
                }

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

                genericList = (ListView)activity.findViewById(R.id.generic_notes);
                genericList.setAdapter(genericAdapter);
                GenericNoteClick clickListener = new GenericNoteClick();
                genericList.setOnItemClickListener(clickListener);
                genericList.setOnItemLongClickListener(clickListener);
                if(genericNotes.size()>0){
                    genericList.setVisibility(View.VISIBLE);
                }

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
        genericAdapter.notifyDataSetChanged();
    }

    public static AllianceExpandableListAdapter getRedAdaper(){
        return redAdaper;
    }

    public static AllianceExpandableListAdapter getBlueAdapter(){
        return blueAdapter;
    }

    public static ListViewArrayAdapter getGenericAdapter(){
        return genericAdapter;
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
            DialogInterface.OnClickListener deleter =
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //delete the event now
                            StartActivity.db.deleteNote(noteId);
                            redAdaper.removeNote(Short.parseShort(noteId));
                            blueAdapter.removeNote(Short.parseShort(noteId));
                            genericAdapter.removeKey(noteId);
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(genericAdapter.keys.size()==0){
                                        genericList.setVisibility(View.GONE);
                                    }
                                }
                            });
                            updateListData();
                            Toast.makeText(activity, "Deleted note from database", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        }
                    };
            new DeleteDialog(activity.getString(R.string.note_deletion_message),deleter)
                    .show(activity.getFragmentManager(),"delete_note");
        }
    };

    class GenericNoteClick implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if(!selectedNote.equals("")) return;
            final short noteId = Short.parseShort(genericAdapter.getKey(i));
            final Note oldNote = StartActivity.db.getNote(noteId);

            new EditNoteDialog(activity.getString(R.string.edit_note_generic_title),oldNote,noteId,genericAdapter).show(activity.getFragmentManager(),"edit_note");
        }

        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            view.setSelected(true);
            GetNotesForMatch.selectedNote = genericAdapter.getKey(i);
            Log.d(Constants.LOG_TAG, "Note selected, id:" + GetNotesForMatch.selectedNote);
            GetNotesForMatch.mActionMode = activity.startActionMode(GetNotesForMatch.mActionModeCallback);
            GetNotesForMatch.updateListData();
            return false;
        }
    }

}
