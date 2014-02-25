package com.plnyyanks.frcnotebook.background;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.plnyyanks.frcnotebook.Constants;
import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.activities.StartActivity;
import com.plnyyanks.frcnotebook.datatypes.Note;

import java.util.ArrayList;

/**
 * Created by phil on 2/24/14.
 */
public class GetNotesForTeam extends AsyncTask<String,String,String> {

    private Activity activity;
    private String teamKey,teamNumber,eventKey;

    public GetNotesForTeam(Activity activity) {
        super();
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... strings) {
        teamKey = strings[0];
        eventKey = strings[1];
        teamNumber = teamKey.substring(3);

        Button addNote = (Button)activity.findViewById(R.id.submit_general_note);
        if(addNote!=null){
            addNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Note newNote = new Note();
                    newNote.setTeamKey(teamKey);
                    newNote.setEventKey(eventKey);
                    newNote.setMatchKey("all");
                    String noteText = ((EditText)activity.findViewById(R.id.new_general_note)).getText().toString();
                    newNote.setNote(noteText);

                    String resultToast;
                    if(StartActivity.db.addNote(newNote) != -1){
                        resultToast = "Note added sucessfully";
                    }else{
                        resultToast = "Error adding note to database";
                    }
                    Toast.makeText(activity, resultToast, Toast.LENGTH_SHORT).show();
                    LinearLayout eventList = (LinearLayout) activity.findViewById(R.id.general_notes);

                    LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);JsonElement element;
                    addNote(newNote,eventList,lparams);
                    EditText addBox = (EditText)activity.findViewById(R.id.new_general_note);
                    addBox.setText("");
                }
            });
        }
        fetchNotes();

        return null;
    }

    protected void fetchNotes(){
        final ArrayList<Note> generalNotes = StartActivity.db.getAllNotes(teamKey,eventKey,"all");
        final ArrayList<Note> matchNotes = StartActivity.db.getAllMatchNotes(teamKey,eventKey);

        final LinearLayout generalList = (LinearLayout) activity.findViewById(R.id.general_notes);
        final LinearLayout matchNotesList = (LinearLayout)activity.findViewById(R.id.match_notes);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(generalNotes.size()>0)
                    generalList.removeAllViews();
                if(matchNotes.size()>0)
                    matchNotesList.removeAllViews();
            }
        });

        for(Note note:generalNotes){
            addNote(note,generalList, Constants.lparams);
        }
        for(Note note:matchNotes){
            addNote(note,matchNotesList,Constants.lparams);
        }
    }

    private void addNote(Note note,final LinearLayout layout,LinearLayout.LayoutParams params){
        final TextView tv=new TextView(activity);
        tv.setLayoutParams(params);
        tv.setText("• " + note.getNote());
        tv.setTextSize(20);
        tv.setLongClickable(true);
        tv.setTag(note.getId());
        tv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final Note oldNote = StartActivity.db.getNote((Short) tv.getTag());
                final EditText noteEditField = new EditText(activity);
                //noteEditField.setId(999);
                noteEditField.setText(oldNote.getNote());

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Note on Team " + teamNumber);
                builder.setView(noteEditField);
                builder.setMessage("Edit your note.");
                builder.setPositiveButton("Update",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                oldNote.setNote(noteEditField.getText().toString());
                                StartActivity.db.updateNote(oldNote);
                                fetchNotes();
                                dialog.cancel();
                            }
                        });

                builder.setNeutralButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                builder.setNegativeButton("Delete",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                StartActivity.db.deleteNote(oldNote);
                                fetchNotes();
                                dialog.cancel();
                            }
                        });
                builder.create().show();
                return false;
            }
        });
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                layout.addView(tv);
            }
        });
    }
}
