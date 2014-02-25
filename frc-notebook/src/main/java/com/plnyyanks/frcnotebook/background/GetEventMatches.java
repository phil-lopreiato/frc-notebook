package com.plnyyanks.frcnotebook.background;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.SparseArray;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;

import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.activities.StartActivity;
import com.plnyyanks.frcnotebook.adapters.EventListArrayAdapter;
import com.plnyyanks.frcnotebook.adapters.ExapandableListAdapter;
import com.plnyyanks.frcnotebook.datatypes.Event;
import com.plnyyanks.frcnotebook.datatypes.ListGroup;
import com.plnyyanks.frcnotebook.datatypes.Match;

import java.util.ArrayList;

/**
 * Created by phil on 2/23/14.
 */
public class GetEventMatches extends AsyncTask <String,String,String>{

    private Activity activity;
    // more efficient than HashMap for mapping integers to objects
    SparseArray<ListGroup> groups = new SparseArray<ListGroup>();

    public GetEventMatches(Activity activity){
        super();
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... strings) {
        createData(strings[0]);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ExpandableListView matchList = (ExpandableListView) activity.findViewById(R.id.match_list);
                ExapandableListAdapter adapter = new ExapandableListAdapter(activity,groups);
                matchList.setAdapter(adapter);

                //hide the progress bar
                ProgressBar prog = (ProgressBar) activity.findViewById(R.id.matches_loading_progress);
                prog.setVisibility(View.GONE);
            }
        });
        return null;
    }

    public void createData(String key) {
        /*for (int j = 0; j < 5; j++) {
            ListGroup group = new ListGroup("Test " + j);
            for (int i = 0; i < 5; i++) {
                group.children.add("Sub Item" + i);
            }
            groups.append(j, group);
        }*/

        Event event = StartActivity.db.getEvent(key);
        ArrayList<Match> allMatches = StartActivity.db.getAllMatches(key),
                qualMatches, qfMatches, sfMatches, fMatches;
        event.sortMatches(allMatches);
        qualMatches = new ArrayList<Match>();
        qualMatches = event.getQuals();
        qfMatches = event.getQuarterFinals();
        sfMatches = event.getSemiFinals();
        fMatches = event.getFinals();

        ArrayList<Match> elimMatches = new ArrayList<Match>();
        elimMatches.addAll(qfMatches);
        elimMatches.addAll(sfMatches);
        elimMatches.addAll(fMatches);

        ListGroup qualGroup = new ListGroup("Qualification Matches ("+qualMatches.size()+")");
        for (Match m : qualMatches) {
            qualGroup.children.add(m.getMatchType()+" "+m.getMatchNumber());
            qualGroup.children_keys.add(m.getMatchKey());
        }
        groups.append(0,qualGroup);

        ListGroup elimGroup = new ListGroup(("Elimination Matches ("+elimMatches.size()+")"));
        for (Match m : elimMatches) {
            elimGroup.children.add(m.getMatchType()+" "+m.getSetNumber()+" Match "+m.getMatchNumber());
            elimGroup.children_keys.add(m.getMatchKey());
        }
        groups.append(1,elimGroup);
    }
}
