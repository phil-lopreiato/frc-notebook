package com.plnyyanks.frcnotebook.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.activities.StartActivity;
import com.plnyyanks.frcnotebook.activities.ViewMatch;
import com.plnyyanks.frcnotebook.background.GetNotesForTeam;
import com.plnyyanks.frcnotebook.datatypes.ListGroup;
import com.plnyyanks.frcnotebook.datatypes.Match;
import com.plnyyanks.frcnotebook.datatypes.Note;

/**
 * Created by phil on 2/25/14.
 */
public class NotesExpandableListAdapter extends ExapandableListAdapter {


    public NotesExpandableListAdapter(Activity act, SparseArray<ListGroup> groups) {
        super(act, groups);
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String children = (String) getChild(groupPosition, childPosition);
        TextView text = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.expandablelist_item, null);
        }
        text = (TextView) convertView.findViewById(R.id.matchlist_item);
        text.setText(children);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Note oldNote = StartActivity.db.getNote(Short.parseShort((String)getChildKey(groupPosition, childPosition)));
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
                                GetNotesForTeam.updateListData();
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
        });
        return convertView;
    }

    private void updateNoteInList(Note note){
        SparseArray<ListGroup> groups = GetNotesForTeam.getListData();
        int index = groups.get(0).children_keys.indexOf(Short.toString(note.getId()));
        if(index == -1){
            //if not found in general notes (groups[0], then check match notes)
            index = groups.get(1).children_keys.indexOf(Short.toString(note.getId()));
            if(index == -1){
                //now, if error - not found anywhere
                return;
            }else{
                //update in match notes group
                groups.get(1).children.set(index,GetNotesForTeam.buildMatchNoteTitle(note));
            }
        }else{
            //update in general notes group
            groups.get(0).children.set(index,GetNotesForTeam.buildGeneralNoteTitle(note));
        }


    }
}
