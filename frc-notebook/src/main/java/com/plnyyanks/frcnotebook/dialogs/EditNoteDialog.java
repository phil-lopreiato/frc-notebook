package com.plnyyanks.frcnotebook.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;

import com.plnyyanks.frcnotebook.activities.StartActivity;
import com.plnyyanks.frcnotebook.adapters.AllianceExpandableListAdapter;
import com.plnyyanks.frcnotebook.adapters.CustomExapandableListAdapter;
import com.plnyyanks.frcnotebook.adapters.NotesExpandableListAdapter;
import com.plnyyanks.frcnotebook.background.GetNotesForMatch;
import com.plnyyanks.frcnotebook.background.GetNotesForTeam;
import com.plnyyanks.frcnotebook.datatypes.Note;

import org.w3c.dom.Text;

/**
 * Created by phil on 3/15/14.
 */
public class EditNoteDialog extends DialogFragment {

    private String teamNumber;
    private Note oldNote;
    private short noteId;
    private CustomExapandableListAdapter adapter;

    public EditNoteDialog(){
        super();
    }

    public EditNoteDialog(String team,Note current, short id,CustomExapandableListAdapter ad){
        this();
        teamNumber = team;
        oldNote = current;
        noteId = id;
        adapter = ad;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final EditText noteEditField = new EditText(getActivity());
        //noteEditField.setId(999);
        noteEditField.setText(noteId!=-1?oldNote.getNote():"");
        if(oldNote.getParent()!= -1) {
            //note is a derivative of a predefined note. Don't allow editing
            noteEditField.setEnabled(false);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Note on Team " + teamNumber);
        builder.setView(noteEditField);
        builder.setPositiveButton("Submit",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        oldNote.setNote(noteEditField.getText().toString());
                        if(noteId==-1){
                            short newId = StartActivity.db.addNote(oldNote);
                            adapter.addNote(StartActivity.db.getNote(newId));
                        }else{
                            StartActivity.db.updateNote(oldNote);
                            adapter.updateNote(oldNote);
                        }
                        if(adapter instanceof AllianceExpandableListAdapter) {
                            GetNotesForMatch.updateListData();
                        }
                        if(adapter instanceof NotesExpandableListAdapter){
                            GetNotesForTeam.updateListData();
                        }
                        dialog.cancel();
                    }
                }
        );

        builder.setNeutralButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }
        );
        return builder.create();
    }
}
