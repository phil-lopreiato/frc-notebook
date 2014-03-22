package com.plnyyanks.frcnotebook.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.plnyyanks.frcnotebook.Constants;
import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.activities.StartActivity;
import com.plnyyanks.frcnotebook.background.GetNotesForMatch;
import com.plnyyanks.frcnotebook.database.PreferenceHandler;
import com.plnyyanks.frcnotebook.datatypes.ListElement;
import com.plnyyanks.frcnotebook.datatypes.Match;
import com.plnyyanks.frcnotebook.datatypes.Note;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by phil on 3/14/14.
 */
public class AddNoteDialog extends DialogFragment {

    private static Activity activity;
    private static Match match;
    private Uri fileUri;

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


        ImageView camIcon = (ImageView)layout.findViewById(R.id.add_note_picture);
        camIcon.setOnClickListener(new AddPictureListener());
        if(PreferenceHandler.getTheme()==R.style.theme_dark){
            camIcon.setBackgroundResource(R.drawable.ic_action_camera_dark);
        }

        //get the teams for this match
        final JsonArray redAlliance = match.getRedAllianceTeams(),
                        blueAlliance = match.getBlueAllianceTeams(),
                        teams = new JsonArray();
        teams.addAll(redAlliance);
        teams.addAll(blueAlliance);

        final String[] team_choices = new String[teams.size()+1];
        team_choices[0] = getResources().getString(R.string.all_teams);
        for(int i=1;i<team_choices.length;i++){
            team_choices[i] = teams.get(i-1).getAsString().substring(3);
        }


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

        final Spinner teamSpinner = (Spinner) layout.findViewById(R.id.team_selector);
        final Spinner noteSpinner = (Spinner) layout.findViewById(R.id.predef_note_selector);
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
        ArrayAdapter teamAdapter = new ArrayAdapter(activity,android.R.layout.simple_spinner_item, team_choices);
        teamAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teamSpinner.setAdapter(teamAdapter);
        teamSpinner.setSelection(0);

        ArrayAdapter noteAdapter = new ArrayAdapter(activity,android.R.layout.simple_spinner_item,note_choice);
        noteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        noteSpinner.setAdapter(noteAdapter);

        builder.setView(layout);

        // Add the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if(noteSpinner.getSelectedItemPosition()==0&&e.getText().toString().isEmpty()){
                    dialog.cancel();
                    return;
                }

                Note newNote = new Note();
                newNote.setMatchKey(match.getMatchKey());
                newNote.setEventKey(match.getParentEvent().getEventKey());
                newNote.setParent(note_choice_ids[noteSpinner.getSelectedItemPosition()]);

                if(noteSpinner.getSelectedItemPosition()==0){
                    newNote.setNote(e.getText().toString());
                }else{
                    newNote.setNote(note_choice[noteSpinner.getSelectedItemPosition()]);
                }

                if(teamSpinner.getSelectedItem().equals(activity.getResources().getString(R.string.all_teams))){
                    //add note for all teams
                    newNote.setTeamKey("all");
                    short newId = StartActivity.db.addNote(newNote);
                    if(newId == -1){
                        dialog.cancel();
                        return;
                    }
                    if(GetNotesForMatch.getGenericAdapter().keys.size() == 0){
                        ListView list = (ListView)activity.findViewById(R.id.generic_notes);
                        list.setVisibility(View.VISIBLE);
                    }
                    newNote = StartActivity.db.getNote(newId);
                    GetNotesForMatch.getGenericAdapter().values.add(new ListElement(newNote.getNote(),Short.toString(newNote.getId())));
                    GetNotesForMatch.getGenericAdapter().keys.add(Short.toString(newNote.getId()));
                    GetNotesForMatch.getGenericAdapter().notifyDataSetChanged();

                }else{
                    //generate team key
                    String team = "frc"+(String)teamSpinner.getSelectedItem();
                    Iterator iterator = redAlliance.iterator();
                    String testTeam;
                    newNote.setTeamKey(team);
                    Log.d(Constants.LOG_TAG, "team key: " + newNote.getTeamKey());
                    while(iterator.hasNext()){
                        testTeam = iterator.next().toString();
                        if(testTeam.equals("\""+team+"\"")){
                            short newId = StartActivity.db.addNote(newNote);
                            newNote = StartActivity.db.getNote(newId);
                            Log.d(Constants.LOG_TAG,"id: "+newId+" team: "+newNote.getTeamKey());
                            GetNotesForMatch.getRedAdaper().addNote(newNote);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(Constants.LOG_TAG,"Result?");
        if (requestCode == Constants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                Toast.makeText(activity, "Image saved to:\n" +
                        data.getData(), Toast.LENGTH_LONG).show();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    class AddPictureListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            // create Intent to take a picture and return control to the calling application
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            fileUri = Uri.fromFile(new File(activity.getFilesDir(), "testImage.jpg")); //TODO better naming!
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

            // start the image capture Intent
            activity.startActivityForResult(intent, Constants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

}
