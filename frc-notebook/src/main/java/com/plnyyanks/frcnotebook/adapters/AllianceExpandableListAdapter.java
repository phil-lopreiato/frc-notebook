package com.plnyyanks.frcnotebook.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.plnyyanks.frcnotebook.Constants;
import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.activities.StartActivity;
import com.plnyyanks.frcnotebook.activities.ViewMatch;
import com.plnyyanks.frcnotebook.background.GetNotesForMatch;
import com.plnyyanks.frcnotebook.background.GetNotesForTeam;
import com.plnyyanks.frcnotebook.datatypes.ListGroup;
import com.plnyyanks.frcnotebook.datatypes.Note;

/**
 * Created by phil on 3/10/14.
 */
public class AllianceExpandableListAdapter extends CustomExapandableListAdapter {

    private SparseArray<ListGroup> groups;

    public AllianceExpandableListAdapter(Activity act, SparseArray<ListGroup> groups) {
        super(act, groups);
        this.groups = groups;
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
                final Note oldNote = StartActivity.db.getNote(Short.parseShort((String) getChildKey(groupPosition, childPosition)));
                final EditText noteEditField = new EditText(activity);
                //noteEditField.setId(999);
                noteEditField.setText(oldNote.getNote());

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Note on Team " + groups.get(groupPosition).getTitle().split(" ")[0]);
                builder.setView(noteEditField);
                builder.setMessage("Edit your note.");
                builder.setPositiveButton("Update",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                oldNote.setNote(noteEditField.getText().toString());
                                StartActivity.db.updateNote(oldNote);
                                updateNoteInList(oldNote);
                                GetNotesForMatch.updateListData();
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
                builder.create().show();
            }
        });
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                view.setSelected(true);
                GetNotesForMatch.selectedNote = groups.get(groupPosition).children_keys.get(childPosition);
                Log.d(Constants.LOG_TAG, "Note selected, id:" + GetNotesForMatch.selectedNote);
                GetNotesForMatch.mActionMode = activity.startActionMode(GetNotesForMatch.mActionModeCallback);
                GetNotesForMatch.updateListData();
                return false;
            }
        });
        if (convertView.isSelected()) {
            convertView.setBackgroundResource(android.R.color.holo_blue_light);
        } else {
            convertView.setBackgroundResource(android.R.color.transparent);
        }
        return convertView;
    }

    private void updateNoteInList(Note note) {
        for (int i = 0; i < groups.size(); i++) {
            int index = groups.get(i).children_keys.indexOf(Short.toString(note.getId()));
            if (index != -1) {
                groups.get(i).children.set(index, Note.buildMatchNoteTitle(note, false, true, true));
            }
        }
    }

    public void removeNote(short id) {
        for (int i = 0; i < groups.size(); i++) {
            int index = groups.get(i).children_keys.indexOf(Short.toString(id));
            if (index != -1) {
                groups.get(i).children.remove(index);
                groups.get(i).children_keys.remove(index);
            }
        }
    }
}
