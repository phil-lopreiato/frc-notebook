package com.plnyyanks.frcnotebook.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.gson.JsonArray;
import com.plnyyanks.frcnotebook.Constants;
import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.activities.StartActivity;
import com.plnyyanks.frcnotebook.adapters.AdapterInterface;
import com.plnyyanks.frcnotebook.background.GetNotesForMatch;
import com.plnyyanks.frcnotebook.background.GetNotesForTeam;
import com.plnyyanks.frcnotebook.datatypes.Match;
import com.plnyyanks.frcnotebook.datatypes.Note;
import com.plnyyanks.frcnotebook.datatypes.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by phil on 3/14/14.
 */
public class AddNoteDialog extends DialogFragment {

    private static Activity activity;
    private static Match match;
    private static String eventKey;
    private static AdapterInterface redAdapter,blueAdapter,genericAdapter,teamViewAdapter;

    public AddNoteDialog(){
        super();
        activity = this.getActivity();
        //match = null;
    }

    public AddNoteDialog(Match m,AdapterInterface redAdapter,AdapterInterface blueAdapter, AdapterInterface genericAdapter){
        this();
        match = m;
        eventKey = match.getParentEvent().getEventKey();
        this.redAdapter = redAdapter;
        this.blueAdapter = blueAdapter;
        this.genericAdapter = genericAdapter;
        this.teamViewAdapter = null;
    }

    public AddNoteDialog(String k, AdapterInterface teamAdapter){
        this();
        match = null;
        eventKey = k;
        this.redAdapter = null;
        this.blueAdapter = null;
        this.genericAdapter = null;
        this.teamViewAdapter = teamAdapter;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        activity = this.getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.add_note_title);
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(activity.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.fragment_add_note,null);

        //if match is set, get the teams for this match
        final JsonArray redAlliance,
                  blueAlliance;
        JsonArray teams;
        String[] team_choices;
        if(match!=null){
            redAlliance = match.getRedAllianceTeams();
            blueAlliance = match.getBlueAllianceTeams();
            teams = new JsonArray();
            teams.addAll(redAlliance);
            teams.addAll(blueAlliance);

            team_choices = new String[teams.size()+1];
            team_choices[0] = getResources().getString(R.string.all_teams);
            for(int i=1;i<team_choices.length;i++){
                team_choices[i] = teams.get(i-1).getAsString().substring(3);
            }
        }else{
            if(GetNotesForTeam.getTeamKey().equals("all")){
                ArrayList<Team> all_teams = StartActivity.db.getAllTeamAtEvent(GetNotesForTeam.getEventKey());
                team_choices = new String[all_teams.size()+1];
                team_choices[0] = activity.getString(R.string.all_teams);
                for(int i=1;i<all_teams.size();i++){
                    team_choices[i] = Integer.toString(all_teams.get(i-1).getTeamNumber());
                }
            }else{
                //we're on the team page, so only allow this team
                team_choices = new String[1];
                team_choices[0] = GetNotesForTeam.getTeamNumber();
            }

            //keep the compiler happy
            redAlliance = new JsonArray();
            blueAlliance = new JsonArray();
        }



        //get predefined notes
        HashMap<Short,String> allPredefNotes = StartActivity.db.getAllDefNotes();
        final String[] note_choice = new String[allPredefNotes.size()+1];
        final short[] note_choice_ids = new short[allPredefNotes.size()+1];
        note_choice[0] = "Custom Note";
        note_choice_ids[0] = -1;
        Iterator<Short> iterator = allPredefNotes.keySet().iterator();
        Short key;
        for(int i=1;i<note_choice.length&&iterator.hasNext();i++){
            key = iterator.next();
            note_choice_ids[i] = key;
            note_choice[i] = allPredefNotes.get(key);
        }

        //get all the matches at this event
        final String[] match_choices;
        final String[] match_keys;
        if(match!=null){
            match_choices = new String[1];
            match_choices[0] = match.getTitle();

            match_keys = new String[1];
            match_keys[0] = match.getMatchKey();
        }else {
            ArrayList<Match> matches = new ArrayList<Match>();
            if(eventKey.equals("all")){
                matches = StartActivity.db.getAllMatchesForTeam(GetNotesForTeam.getTeamKey());
            }else{
                matches = StartActivity.db.getAllMatches(eventKey,GetNotesForTeam.getTeamKey());
            }
            match_choices = new String[matches.size()+1];
            match_keys = new String[matches.size()+1];
            match_choices[0] = getString(R.string.generic_note);
            match_keys[0] = "all";
            for(int i=1;i<match_choices.length;i++){
                match_choices[i] = matches.get(i-1).getTitle(eventKey.equals("all"));
                match_keys[i] = matches.get(i-1).getMatchKey();
            }
        }

        final Spinner teamSpinner = (Spinner) layout.findViewById(R.id.team_selector);
        final Spinner noteSpinner = (Spinner) layout.findViewById(R.id.predef_note_selector);
        final Spinner matchSpinner= (Spinner) layout.findViewById(R.id.match_selector);
        final EditText e = (EditText)layout.findViewById(R.id.note_contents);
        noteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){
                    e.setVisibility(View.VISIBLE);
                }else{
                    e.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        final ArrayAdapter teamAdapter = new ArrayAdapter(activity,android.R.layout.simple_spinner_item, team_choices);
        teamAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teamSpinner.setAdapter(teamAdapter);
        teamSpinner.setEnabled(match!=null || GetNotesForTeam.getTeamKey().equals("all"));

        ArrayAdapter noteAdapter = new ArrayAdapter(activity,android.R.layout.simple_spinner_item,note_choice);
        noteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        noteSpinner.setAdapter(noteAdapter);

        ArrayAdapter matchAdapter = new ArrayAdapter(activity,android.R.layout.simple_spinner_item,match_choices);
        matchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        matchSpinner.setAdapter(matchAdapter);
        matchSpinner.setEnabled(match==null);

        builder.setView(layout);

        // Add the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if(noteSpinner.getSelectedItemPosition()==0&&e.getText().toString().isEmpty()){
                    dialog.cancel();
                    return;
                }
                if(match != null) {
                    Note newNote = new Note();
                    newNote.setMatchKey(match_keys[matchSpinner.getSelectedItemPosition()]);
                    newNote.setEventKey(match.getParentEvent().getEventKey());
                    newNote.setParent(note_choice_ids[noteSpinner.getSelectedItemPosition()]);

                    if (noteSpinner.getSelectedItemPosition() == 0) {
                        newNote.setNote(e.getText().toString());
                    } else {
                        newNote.setNote(note_choice[noteSpinner.getSelectedItemPosition()]);
                    }

                    if (teamSpinner.getSelectedItem().equals(activity.getResources().getString(R.string.all_teams))) {
                        //add note for all teams
                        newNote.setTeamKey("all");
                        short newId = StartActivity.db.addNote(newNote);
                        if (newId == -1) {
                            dialog.cancel();
                            return;
                        }
                        if (GetNotesForMatch.getGenericAdapter().keys.size() == 0) {
                            ListView list = (ListView) activity.findViewById(R.id.generic_notes);
                            list.setVisibility(View.VISIBLE);
                        }
                        newNote = StartActivity.db.getNote(newId);
                        genericAdapter.addNote(newNote);

                    } else {
                        //generate team key
                        String team = "frc" + (String) teamSpinner.getSelectedItem();
                        Iterator iterator = redAlliance.iterator();
                        String testTeam;
                        newNote.setTeamKey(team);
                        Log.d(Constants.LOG_TAG, "team key: " + newNote.getTeamKey());
                        while (iterator.hasNext()) {
                            testTeam = iterator.next().toString();
                            if (testTeam.equals("\"" + team + "\"")) {
                                short newId = StartActivity.db.addNote(newNote);
                                newNote = StartActivity.db.getNote(newId);
                                Log.d(Constants.LOG_TAG, "id: " + newId + " team: " + newNote.getTeamKey());
                                redAdapter.addNote(newNote);
                                dialog.cancel();
                                return;
                            }
                        }
                        iterator = blueAlliance.iterator();
                        while (iterator.hasNext()) {
                            testTeam = iterator.next().toString();
                            if (testTeam.equals("\"" + team + "\"")) {
                                short newId = StartActivity.db.addNote(newNote);
                                blueAdapter.addNote(StartActivity.db.getNote(newId));
                                ;
                                dialog.cancel();
                                return;
                            }
                        }
                    }
                }else{
                    //add the note on the team view activity
                    Note newNote = new Note();
                    newNote.setMatchKey(match_keys[matchSpinner.getSelectedItemPosition()]);
                    if(newNote.getMatchKey().equals("all")){
                        newNote.setEventKey(eventKey);
                    }else{
                        newNote.setEventKey(newNote.getMatchKey().split("_")[0]);
                    }
                    newNote.setParent(note_choice_ids[noteSpinner.getSelectedItemPosition()]);
                    String team = "frc" + (String) teamSpinner.getSelectedItem();
                    newNote.setTeamKey(team.equals(activity.getString(R.string.all_teams))?"all":team);

                    if (noteSpinner.getSelectedItemPosition() == 0) {
                        //set note to custom text
                        newNote.setNote(e.getText().toString());
                    } else {
                        //set note to a predefined note
                        newNote.setNote(note_choice[noteSpinner.getSelectedItemPosition()]);
                    }

                    short newId = StartActivity.db.addNote(newNote);
                    if (newId == -1) {
                        dialog.cancel();
                        return;
                    }
                    newNote = StartActivity.db.getNote(newId);
                    teamViewAdapter.addNote(newNote);
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.dismiss();
            }
        });

        Dialog out = builder.create();
        out.getWindow().clearFlags( WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        out.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return out;
    }

    private String[] getTeamsForMatch(String matchKey) {
        if (matchKey.equals("all")) {
            return null;
        }
        return getTeamsForMatch(StartActivity.db.getMatch(matchKey));
    }

    private String[] getTeamsForMatch(Match m){
        //get the teams for this match
        final JsonArray redAlliance = m.getRedAllianceTeams(),
                blueAlliance = m.getBlueAllianceTeams(),
                teams = new JsonArray();
        teams.addAll(redAlliance);
        teams.addAll(blueAlliance);

        String[] team_choices = new String[teams.size() + 1];
        team_choices[0] = getResources().getString(R.string.all_teams);
        for (int i = 1; i < team_choices.length; i++) {
            team_choices[i] = teams.get(i - 1).getAsString().substring(3);
        }
        return team_choices;
    }


}
