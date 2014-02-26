package com.plnyyanks.frcnotebook.background;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.plnyyanks.frcnotebook.Constants;
import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.activities.StartActivity;
import com.plnyyanks.frcnotebook.adapters.ExapandableListAdapter;
import com.plnyyanks.frcnotebook.adapters.NotesExpandableListAdapter;
import com.plnyyanks.frcnotebook.datatypes.Event;
import com.plnyyanks.frcnotebook.datatypes.ListGroup;
import com.plnyyanks.frcnotebook.datatypes.Match;
import com.plnyyanks.frcnotebook.datatypes.Note;

import java.util.ArrayList;

/**
 * Created by phil on 2/24/14.
 */
public class GetNotesForTeam extends AsyncTask<String,String,String> {

    private Activity activity;
    private static String teamKey,teamNumber,eventKey, eventTitle;
    private static SparseArray<ListGroup> groups = new SparseArray<ListGroup>();
    private static NotesExpandableListAdapter adapter;

    public GetNotesForTeam(Activity activity) {
        super();
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... strings) {
        teamKey = strings[0];
        eventKey = strings[1];
        teamNumber = teamKey.substring(3);
        eventTitle = strings[2];

        Button addNote = (Button)activity.findViewById(R.id.submit_general_note);
        if(addNote!=null){
            addNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Note newNote = new Note();
                    newNote.setTeamKey(teamKey);
                    newNote.setEventKey(eventKey);
                    newNote.setMatchKey("all");
                    String noteText = ((EditText)activity.findViewById(R.id.new_general_note)).getText().toString();
                    newNote.setNote(noteText);

                    String resultToast;
                    short dbResult = StartActivity.db.addNote(newNote);
                    if(dbResult != -1){
                        resultToast = "Note added sucessfully";
                        ExpandableListView noteList = (ExpandableListView)activity.findViewById(R.id.note_list);
                        newNote.setId(dbResult);
                        ListGroup group = groups.get(0);
                        group.children.add(newNote.getNote());
                        group.children_keys.add(Integer.toString(newNote.getId()));
                        group.updateTitle("General Notes ("+group.children.size()+")");
                        updateListData();
                    }else{
                        resultToast = "Error adding note to database";
                    }
                    Toast.makeText(activity, resultToast, Toast.LENGTH_SHORT).show();

                    EditText addBox = (EditText)activity.findViewById(R.id.new_general_note);
                    addBox.setText("");
                }
            });
        }

        createData();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ExpandableListView matchList = (ExpandableListView) activity.findViewById(R.id.note_list);
                adapter = new NotesExpandableListAdapter(activity,groups);
                matchList.setAdapter(adapter);

                //hide the progress bar
                ProgressBar prog = (ProgressBar) activity.findViewById(R.id.notes_loading_progress);
                prog.setVisibility(View.GONE);
            }
        });

        return null;
    }

    private void createData() {
        final ArrayList<Note> generalNotes = StartActivity.db.getAllNotes(teamKey,eventKey,"all");
        final ArrayList<Note> matchNotes = StartActivity.db.getAllMatchNotes(teamKey,eventKey);

       ListGroup generalNoteGroup = new ListGroup("General Notes ("+generalNotes.size()+")");
        for (Note n : generalNotes) {
            generalNoteGroup.children.add(Note.buildGeneralNoteTitle(n, eventTitle.equals("all")));
            generalNoteGroup.children_keys.add(Integer.toString(n.getId()));
        }
        groups.append(0,generalNoteGroup);

        ListGroup matchNoteGroup = new ListGroup(("Match Notes ("+matchNotes.size()+")"));
        for (Note n : matchNotes) {
            matchNoteGroup.children.add(Note.buildMatchNoteTitle(n, eventTitle.equals("all")));
            matchNoteGroup.children_keys.add(Integer.toString(n.getId()));
        }
        groups.append(1,matchNoteGroup);
    }

    public static void updateListData(){
        adapter.notifyDataSetChanged();
    }

    public static SparseArray<ListGroup> getListData() {
        return groups;
    }

    public static String getTeamKey() {
        return teamKey;
    }

    public static String getTeamNumber() {
        return teamNumber;
    }

    public static String getEventKey() {
        return eventKey;
    }

    public static String getEventTitle(){
        return eventTitle;
    }
}