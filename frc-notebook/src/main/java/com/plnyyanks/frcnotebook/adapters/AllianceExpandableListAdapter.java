package com.plnyyanks.frcnotebook.adapters;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.plnyyanks.frcnotebook.Constants;
import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.activities.StartActivity;
import com.plnyyanks.frcnotebook.activities.ViewMatch;
import com.plnyyanks.frcnotebook.activities.ViewTeam;
import com.plnyyanks.frcnotebook.background.GetNotesForMatch;
import com.plnyyanks.frcnotebook.database.PreferenceHandler;
import com.plnyyanks.frcnotebook.datatypes.ListGroup;
import com.plnyyanks.frcnotebook.datatypes.Note;
import com.plnyyanks.frcnotebook.dialogs.EditNoteDialog;

/**
 * File created by phil on 3/10/14.
 * Copyright 2014, Phil Lopreiato
 * This file is part of FRC Notebook.
 * FRC Notebook is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * FRC Notebook is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with FRC Notebook. If not, see http://www.gnu.org/licenses/.
 */
public class AllianceExpandableListAdapter extends CustomExapandableListAdapter {

    private SparseArray<ListGroup> groups;

    public AllianceExpandableListAdapter(Activity act, SparseArray<ListGroup> groups) {
        super(act, groups);
        this.groups = groups;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.expandablelist_team_group, null);
        }

        final ListGroup group = (ListGroup) getGroup(groupPosition);
        CheckedTextView textView = (CheckedTextView)convertView.findViewById(R.id.matchlist_group);
        textView.setText(group.string);
        textView.setChecked(isExpanded);

        ImageView infoButton = (ImageView)convertView.findViewById(R.id.group_more_info);
        if(PreferenceHandler.getTheme() == R.style.theme_dark){
            infoButton.setBackgroundResource(R.drawable.ic_action_about_dark);
        }
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String teamKey = "frc"+group.string.split(" ")[0];
                ViewTeam.setTeam(teamKey);
                activity.startActivity(new Intent(activity,ViewTeam.class));
            }
        });

        return convertView;
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
                final short noteId = Short.parseShort((String) getChildKey(groupPosition, childPosition));
                String teamNumber = groups.get(groupPosition).getTitle().split(" ")[0];
                final Note oldNote = noteId!=-1?StartActivity.db.getNote(noteId):new Note(ViewMatch.eventKey,ViewMatch.matchKey,"frc"+teamNumber,"");

                new EditNoteDialog(activity.getString(R.string.edit_note_team_title)+teamNumber,oldNote,noteId,AllianceExpandableListAdapter.this).show(activity.getFragmentManager(),"edit_note");
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

    public void updateNote(Note note) {
        for (int i = 0; i < groups.size(); i++) {
            int index = groups.get(i).children_keys.indexOf(Short.toString(note.getId()));
            if (index != -1) {
                groups.get(i).children.set(index, Note.buildMatchNoteTitle(note, false, true, true));
                return;
            }
        }
    }

    public void updateListData(){
        GetNotesForMatch.updateListData();
    }

    public void addNote(Note note){
        String team;
        for (int i = 0; i < groups.size(); i++) {
            team = groups.get(i).getTitle().split(" ")[0];
            Log.d(Constants.LOG_TAG,"Team key: "+note.getTeamKey()+" test: "+team);
            if(note.getTeamKey().contains(team)){
                groups.get(i).children.add(Note.buildMatchNoteTitle(note,false,true,true));
                groups.get(i).children_keys.add(Short.toString(note.getId()));
                groups.get(i).updateTitle(note.getTeamKey().substring(3)+(groups.get(i).children_keys.size()>0?(" ("+ groups.get(i).children_keys.size()+")"):""));
                Log.d(Constants.LOG_TAG,"added note to adapter, id "+note.getId());
                GetNotesForMatch.updateListData();
                return;
            }
        }
        notifyDataSetChanged();
    }

    public void removeNote(short id) {
        for (int i = 0; i < groups.size(); i++) {
            int index = groups.get(i).children_keys.indexOf(Short.toString(id));
            if (index != -1) {
                groups.get(i).children.remove(index);
                groups.get(i).children_keys.remove(index);
                String team = groups.get(i).getTitle().split(" ")[0];
                groups.get(i).updateTitle(team+(groups.get(i).children_keys.size()>0?(" ("+ groups.get(i).children_keys.size()+")"):""));
                return;
            }
        }
        notifyDataSetChanged();
    }
}
