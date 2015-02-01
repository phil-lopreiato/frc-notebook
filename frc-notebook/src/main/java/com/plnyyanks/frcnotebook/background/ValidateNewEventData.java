package com.plnyyanks.frcnotebook.background;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.plnyyanks.frcnotebook.Constants;
import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.activities.StartActivity;
import com.plnyyanks.frcnotebook.datafeed.USFIRSTParser;
import com.plnyyanks.frcnotebook.datatypes.Event;
import com.plnyyanks.frcnotebook.datatypes.Team;
import com.plnyyanks.frcnotebook.dialogs.ProgressDialog;

/**
 * File created by phil on 4/18/14.
 * Copyright 2015, Phil Lopreiato
 * This file is part of FRC Notebook
 * FRC Notebook is licensed under the MIT License
 * (http://opensource.org/licenses/MIT)
 */
public class ValidateNewEventData extends AsyncTask<String,String,String>{

    Activity activity;
    //String eventTitle,eventShort,eventKey,eventStart,eventEnd,eventTeams,eventMatch;
    String[] params; //params in ^ order
    ProgressDialog progress;
    Event e;

    public ValidateNewEventData(Activity in){
        activity = in;
        params = new String[7];
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progress = new ProgressDialog("Creating Event...");
        progress.show(activity.getFragmentManager(),"createEvent");

        params[0] = getText((EditText)activity.findViewById(R.id.event_title));
        params[1] = getText((EditText)activity.findViewById(R.id.event_short));
        params[2] = getText((EditText)activity.findViewById(R.id.event_key));
        params[3] = getText((Button)  activity.findViewById(R.id.event_start));
        params[4] = getText((Button)  activity.findViewById(R.id.event_end));
        params[5] = getText((EditText)activity.findViewById(R.id.teams_attending));
        params[6] = getText((EditText)activity.findViewById(R.id.match_schedule));
    }

    @Override
    protected String doInBackground(String... args) {
        for(int i=0;i<5;i++){
            //make sure every requried field is set
            Log.d(Constants.LOG_TAG, "Checking " + i);
            if(params[i] == null || params[i].equals("")){
                Log.w(Constants.LOG_TAG,"Error: "+params[i]);
                return "Error: please enter a required field";
            }
        }

        e = new Event();
        e.setOfficial(false);
        e.setEventName(params[0]);
        e.setShortName(params[1]);
        e.setEventYear(Integer.parseInt(params[3].split("-")[0]));
        e.setEventKey(e.getEventYear()+params[2]);
        e.setEventStart(params[3]+"T00:00:00");
        e.setEventEnd(params[4]+"T00:00:00");

        long eventAdd = StartActivity.db.addEvent(e);
        if(eventAdd == -1){
            return "Error adding event to database";
        }

        //check for teams attending
        if(params[5] != null && !params[5].equals("")){
            //add event for teams
            String[] teams = params[5].split(",");
            if(addTeams(teams) == -1){
                return "Error adding teams numbers to database";
            }
        }

        //check for match schedule
        if(params[6] != null && !params[6].equals("")){
            //add match schedule
            new AddMatchesFromURL(activity).execute(params[6],e.getEventKey());
        }

        return "";
    }

    @Override
    protected void onPostExecute(String success) {
        super.onPostExecute(success);
        progress.dismiss();
        String toast = "";
        if(success.equals("")){
            toast = "Event added";
            Toast.makeText(activity,toast,Toast.LENGTH_SHORT).show();
            activity.startActivity(new Intent(activity,StartActivity.class));
        }else{
            Toast.makeText(activity,success,Toast.LENGTH_SHORT).show();
        }

    }

    private long addTeams(String[] teams){
        Team team;
        long result=0;
        for(int i=0;i<teams.length && result != -1;i++){
            team = new Team();
            try{
                team.setTeamNumber(Integer.parseInt(teams[i].trim()));
                team.setTeamKey("frc"+teams[i].trim());
                team.addEvent(e.getEventKey());
                Log.d(Constants.LOG_TAG,"Adding team "+team.getTeamKey());
                result = StartActivity.db.addTeam(team);
            }catch(Exception e){
                Log.w(Constants.LOG_TAG,"Exception while adding teams: "+e.getStackTrace().toString());
                return -1;
            }
        }
        return result;
    }

    private String getText(EditText view){
        Log.d(Constants.LOG_TAG,"Getting text from: "+view.getHint());
        if(view != null && view.getText() != null){
            return view.getText().toString().trim();
        }else{
            Log.w(Constants.LOG_TAG,"Null Item in Activity!");
           return "";
        }
    }
    private String getText(Button view){
        if(view != null && view.getText() != null){
            return view.getText().toString().trim();
        }else{
            return "";
        }
    }
}
