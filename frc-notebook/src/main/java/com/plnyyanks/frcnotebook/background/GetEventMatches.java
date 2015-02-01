package com.plnyyanks.frcnotebook.background;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.activities.StartActivity;
import com.plnyyanks.frcnotebook.adapters.ListViewArrayAdapter;
import com.plnyyanks.frcnotebook.adapters.MatchListExpandableListAdapter;
import com.plnyyanks.frcnotebook.datatypes.Event;
import com.plnyyanks.frcnotebook.datatypes.ListElement;
import com.plnyyanks.frcnotebook.datatypes.ListGroup;
import com.plnyyanks.frcnotebook.datatypes.ListItem;
import com.plnyyanks.frcnotebook.datatypes.Match;
import com.plnyyanks.frcnotebook.datatypes.Note;
import com.plnyyanks.frcnotebook.datatypes.Team;

import java.util.ArrayList;
import java.util.Collections;

/**
 * File created by phil on 3/1/14.
 * Copyright 2015, Phil Lopreiato
 * This file is part of FRC Notebook
 * FRC Notebook is licensed under the MIT License
 * (http://opensource.org/licenses/MIT)
 */
public class GetEventMatches extends AsyncTask <String,String,String>{

    private static Activity activity;
    // more efficient than HashMap for mapping integers to objects
    SparseArray<ListGroup> groups;
    private String eventKey;

    public GetEventMatches(Activity activityIn){
        super();
        activity = activityIn;
    }

    public static void setActivity(Activity activityIn) {
        activity = activityIn;
    }

    @Override
    protected String doInBackground(String... strings) {
        eventKey = strings[0];
        createData(strings[0]);
        final String[] sortValues = activity.getResources().getStringArray(R.array.match_sort_options);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ExpandableListView matchList = (ExpandableListView) activity.findViewById(R.id.match_list);
                if(matchList==null)
                    return;
                MatchListExpandableListAdapter adapter = new MatchListExpandableListAdapter(activity,groups);
                matchList.setAdapter(adapter);
                matchList.expandGroup(0);
                matchList.expandGroup(1);

                //create an adapter for the spinner
                ArrayAdapter<String> sortAdapter = new ArrayAdapter<String>(activity,android.R.layout.simple_spinner_item,android.R.id.text1,sortValues);
                sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                Spinner sortSpinner = (Spinner)activity.findViewById(R.id.match_sort_order);
                sortSpinner.setAdapter(sortAdapter);
                sortSpinner.setOnItemSelectedListener(new ClickListener());

                //hide the progress bar
                ProgressBar prog = (ProgressBar) activity.findViewById(R.id.matches_loading_progress);
                prog.setVisibility(View.GONE);
            }
        });
        return null;
    }

    private void createData(String key) {
        /*for (int j = 0; j < 5; j++) {
            ListGroup group = new ListGroup("Test " + j);
            for (int i = 0; i < 5; i++) {
                group.children.add("Sub Item" + i);
            }
            groups.append(j, group);
        }*/
        groups = new SparseArray<ListGroup>();
        Event event = StartActivity.db.getEvent(key);
        ArrayList<Match> allMatches = StartActivity.db.getAllMatches(key),
                qualMatches, qfMatches, sfMatches, fMatches;
        event.sortMatches(allMatches);
        qualMatches = event.getQuals();
        qfMatches = event.getQuarterFinals();
        sfMatches = event.getSemiFinals();
        fMatches = event.getFinals();

        ArrayList<Match> elimMatches = new ArrayList<Match>();
        elimMatches.addAll(qfMatches);
        elimMatches.addAll(sfMatches);
        elimMatches.addAll(fMatches);

        ListGroup qualGroup = new ListGroup("Qualification Matches ("+qualMatches.size()+")");
        Collections.sort(qualMatches);
        for (Match m : qualMatches) {
            qualGroup.children.add(m.getTitle(false,true));
            qualGroup.children_keys.add(m.getMatchKey());
        }
        groups.append(0,qualGroup);

        ListGroup elimGroup = new ListGroup(("Elimination Matches ("+elimMatches.size()+")"));
        Collections.sort(elimMatches);
        for (Match m : elimMatches) {
            elimGroup.children.add(m.getTitle(false,true));
            elimGroup.children_keys.add(m.getMatchKey());
        }
        groups.append(1,elimGroup);
    }

    private class ClickListener implements android.widget.AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            switch(position){
                default:
                case 0:
                    //sort by team number
                    Match.setSortType(Match.SORT_TYPES.MATCH_NO);
                    break;
                case 1:
                    //sort by notes, asc
                    Match.setSortType(Match.SORT_TYPES.NUM_NOTES_ASC);
                    break;
                case 2:
                    //sort by notes, desc
                    Match.setSortType(Match.SORT_TYPES.NUM_NOTES_DSC);
                    break;
            }
            createData(eventKey);

            ExpandableListView matchList = (ExpandableListView) activity.findViewById(R.id.match_list);
            MatchListExpandableListAdapter adapter = new MatchListExpandableListAdapter(activity,groups);
            matchList.setAdapter(adapter);
            matchList.expandGroup(0);
            matchList.expandGroup(1);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}
