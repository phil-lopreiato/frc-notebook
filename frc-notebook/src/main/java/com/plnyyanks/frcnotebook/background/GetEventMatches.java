package com.plnyyanks.frcnotebook.background;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.SparseArray;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;

import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.activities.StartActivity;
import com.plnyyanks.frcnotebook.adapters.MatchListExpandableListAdapter;
import com.plnyyanks.frcnotebook.datatypes.Event;
import com.plnyyanks.frcnotebook.datatypes.ListGroup;
import com.plnyyanks.frcnotebook.datatypes.Match;
import com.plnyyanks.frcnotebook.datatypes.Note;

import java.util.ArrayList;

/**
 * Created by phil on 2/23/14.
 */
public class GetEventMatches extends AsyncTask <String,String,String>{

    private static Activity activity;
    // more efficient than HashMap for mapping integers to objects
    SparseArray<ListGroup> groups = new SparseArray<ListGroup>();

    public GetEventMatches(Activity activityIn){
        super();
        activity = activityIn;
    }

    public static void setActivity(Activity activityIn) {
        activity = activityIn;
    }

    @Override
    protected String doInBackground(String... strings) {
        createData(strings[0]);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ExpandableListView matchList = (ExpandableListView) activity.findViewById(R.id.match_list);
                if(matchList==null)
                    return;
                MatchListExpandableListAdapter adapter = new MatchListExpandableListAdapter(activity,groups);
                matchList.setAdapter(adapter);

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
        ArrayList<Note> notes;
        for (Match m : qualMatches) {
            notes = StartActivity.db.getAllNotes("all",event.getEventKey(),m.getMatchKey());
            qualGroup.children.add(m.getMatchType()+" "+m.getMatchNumber()+(notes.size()>0?" ("+notes.size()+" notes)":""));
            qualGroup.children_keys.add(m.getMatchKey());
        }
        groups.append(0,qualGroup);

        ListGroup elimGroup = new ListGroup(("Elimination Matches ("+elimMatches.size()+")"));
        for (Match m : elimMatches) {
            notes = StartActivity.db.getAllNotes("all",event.getEventKey(),m.getMatchKey());
            elimGroup.children.add(m.getMatchType()+" "+m.getSetNumber()+" Match "+m.getMatchNumber()+(notes.size()>0?" ("+notes.size()+" notes)":""));
            elimGroup.children_keys.add(m.getMatchKey());
        }
        groups.append(1,elimGroup);
    }
}
