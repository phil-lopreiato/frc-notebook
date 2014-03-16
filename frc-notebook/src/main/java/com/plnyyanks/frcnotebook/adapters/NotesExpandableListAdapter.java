package com.plnyyanks.frcnotebook.adapters;

import android.app.Activity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.plnyyanks.frcnotebook.Constants;
import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.activities.StartActivity;
import com.plnyyanks.frcnotebook.background.GetNotesForTeam;
import com.plnyyanks.frcnotebook.datatypes.ListGroup;
import com.plnyyanks.frcnotebook.datatypes.Note;
import com.plnyyanks.frcnotebook.dialogs.EditNoteDialog;

/**
 * Created by phil on 2/25/14.
 */
public class NotesExpandableListAdapter extends CustomExapandableListAdapter {

    private SparseArray<ListGroup> groups;

    public NotesExpandableListAdapter(Activity act, SparseArray<ListGroup> groups) {
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
                final Note oldNote = StartActivity.db.getNote(Short.parseShort((String)getChildKey(groupPosition, childPosition)));

                new EditNoteDialog(activity.getString(R.string.edit_note_team_title)+GetNotesForTeam.getTeamNumber(),oldNote,oldNote.getId(),NotesExpandableListAdapter.this).show(activity.getFragmentManager(),"edit_note");
            }
        });
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                view.setSelected(true);
                GetNotesForTeam.selectedNote = groups.get(groupPosition).children_keys.get(childPosition);
                Log.d(Constants.LOG_TAG,"Note selected, id:"+GetNotesForTeam.selectedNote);
                GetNotesForTeam.mActionMode = activity.startActionMode(GetNotesForTeam.mActionModeCallback);
                GetNotesForTeam.updateListData();
                return false;
            }
        });
        if(convertView.isSelected()){
            convertView.setBackgroundResource(android.R.color.holo_blue_light);
        }else{
            convertView.setBackgroundResource(android.R.color.transparent);
        }
        return convertView;
    }

    public void updateNote(Note note){
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
                groups.get(1).children.set(index,Note.buildMatchNoteTitle(note,GetNotesForTeam.getEventTitle().equals("all"),true));
            }
        }else{
            //update in general notes group
            groups.get(0).children.set(index,Note.buildGeneralNoteTitle(note,GetNotesForTeam.getEventTitle().equals("all")));
        }


    }

    public void removeNote(short id){
        SparseArray<ListGroup> groups = GetNotesForTeam.getListData();
        int index = groups.get(0).children_keys.indexOf(Short.toString(id));
        if(index == -1){
            //if not found in general notes (groups[0], then check match notes)
            index = groups.get(1).children_keys.indexOf(Short.toString(id));
            if(index == -1){
                Log.w(Constants.LOG_TAG, "Tried to delete nonexistant note with id:" + id);
                return;
            }else{
                //delete from match notes
                groups.get(1).children.remove(index);
                groups.get(1).children_keys.remove(index);
                groups.get(1).updateTitle("Match Notes ("+groups.get(1).children.size()+")");
                Log.i(Constants.LOG_TAG,"Delete match note with id:"+id);
            }
        }else{
           //delete from general notes
            groups.get(0).children.remove(index);
            groups.get(0).children_keys.remove(index);
            groups.get(0).updateTitle("General Notes ("+groups.get(0).children.size()+")");
            Log.i(Constants.LOG_TAG,"Delete general note with id:"+id);
        }
    }

    public void updateListData(){
        notifyDataSetChanged();
    }
}
