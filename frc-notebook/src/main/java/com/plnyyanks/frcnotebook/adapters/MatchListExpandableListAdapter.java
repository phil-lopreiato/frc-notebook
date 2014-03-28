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
 * Created by phil on 2/25/14.
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
