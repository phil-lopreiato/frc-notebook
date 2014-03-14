package com.plnyyanks.frcnotebook.activities;

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
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.gson.JsonArray;
import com.plnyyanks.frcnotebook.Constants;
import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.background.GetMatchInfo;
import com.plnyyanks.frcnotebook.background.GetNotesForMatch;
import com.plnyyanks.frcnotebook.datatypes.Match;
import com.plnyyanks.frcnotebook.datatypes.Note;

import java.util.Iterator;

/**
 * Created by phil on 3/14/14.
 */
public class AddNoteDialog extends DialogFragment {

    private static Activity activity;
    private static Match match;

    public AddNoteDialog(){
        super();
        activity = this.getActivity();
    }

    public AddNoteDialog(Match m){
        this();
        match = m;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        activity = this.getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.add_note_title);
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(activity.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.fragment_add_note,null);

        //get the teams for this match
        final JsonArray redAlliance = match.getRedAllianceTeams(),
                        blueAlliance = match.getBlueAllianceTeams(),
                        teams = new JsonArray();
        teams.addAll(redAlliance);
        teams.addAll(blueAlliance);

        final String[] team_choices = new String[teams.size()/*+1*/];
        //team_choices[0] = getResources().getString(R.string.all_teams);
        for(int i=/*1*/0;i<team_choices.length;i++){
            team_choices[i] = teams.get(i/*-1*/).getAsString().substring(3);
        }
        final Spinner s = (Spinner) layout.findViewById(R.id.team_selector);
        final EditText e = (EditText)layout.findViewById(R.id.note_contents);
        ArrayAdapter adapter = new ArrayAdapter(activity,android.R.layout.simple_spinner_item, team_choices);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
        //s.setSelection(0);
        builder.setView(layout);

        // Add the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if(e.getText().toString().isEmpty()){
                    dialog.cancel();
                    return;
                }

                Note newNote = new Note();
                newNote.setNote(e.getText().toString());
                newNote.setMatchKey(match.getMatchKey());
                newNote.setEventKey(match.getParentEvent().getEventKey());

                if(s.getSelectedItemPosition()==0){
                    //add note for all teams
                    newNote.setTeamKey("all");
                    //TODO implement this
                }else{
                    //generate team key
                    String team = "frc"+(String)s.getSelectedItem();
                    Iterator iterator = redAlliance.iterator();
                    String testTeam;
                    newNote.setTeamKey(team);
                    while(iterator.hasNext()){
                        testTeam = iterator.next().toString();
                        if(testTeam.equals("\""+team+"\"")){
                            short newId = StartActivity.db.addNote(newNote);
                            GetNotesForMatch.getRedAdaper().addNote(StartActivity.db.getNote(newId));;
                            dialog.cancel();
                            return;
                        }
                    }
                    iterator = blueAlliance.iterator();
                    while(iterator.hasNext()){
                        testTeam = iterator.next().toString();
                        if(testTeam.equals("\""+team+"\"")){
                            short newId = StartActivity.db.addNote(newNote);
                            GetNotesForMatch.getBlueAdapter().addNote(StartActivity.db.getNote(newId));;
                            dialog.cancel();
                            return;
                        }
                    }
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
}
