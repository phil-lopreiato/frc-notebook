package com.plnyyanks.frcnotebook.adapters;

import android.app.Activity;
import android.content.Intent;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.activities.ViewMatch;
import com.plnyyanks.frcnotebook.datatypes.ListGroup;
import com.plnyyanks.frcnotebook.datatypes.Note;

/**
 * File created by phil on 2/25/2014.
 * Copyright 2014, Phil Lopreiato
 * This file is part of FRC Notebook.
 * FRC Notebook is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * FRC Notebook is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with FRC Notebook. If not, see http://www.gnu.org/licenses/.
 */
public class MatchListExpandableListAdapter extends CustomExapandableListAdapter {


    public MatchListExpandableListAdapter(Activity act, SparseArray<ListGroup> groups) {
        super(act, groups);
    }

    @Override
    public void addNote(Note note) {

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
                ViewMatch.setMatchKey((String) getChildKey(groupPosition, childPosition));
                Intent intent = new Intent(activity, ViewMatch.class);
                activity.startActivity(intent);
            }
        });
        return convertView;
    }

    public void updateListData(){
        notifyDataSetChanged();
    }
}
