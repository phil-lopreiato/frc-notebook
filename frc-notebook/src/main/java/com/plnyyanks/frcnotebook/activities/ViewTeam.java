package com.plnyyanks.frcnotebook.activities;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.plnyyanks.frcnotebook.Constants;
import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.datatypes.Event;
import com.plnyyanks.frcnotebook.datatypes.Note;
import com.plnyyanks.frcnotebook.datatypes.Team;

import java.util.ArrayList;

public class ViewTeam extends Activity implements ActionBar.TabListener {

    protected static String teamKey;
    protected static int teamNumber;
    protected static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(StartActivity.getThemeFromPrefs());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_team);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .commit();
        }

        context = this;

        ActionBar bar = getActionBar();
        bar.setTitle("Team "+teamNumber);

        //tab for team overview
        ActionBar.Tab teamOverviewTab = bar.newTab();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        teamOverviewTab.setText("All Notes");
        teamOverviewTab.setTag("all");
        teamOverviewTab.setTabListener(this);
        bar.addTab(teamOverviewTab);

        //add an actionbar tab for every event the team is competing at
        Team team = StartActivity.db.getTeam(teamKey);
        ArrayList<String> events = team.getTeamEvents();
        for(String eventKey:events){
            Event event = StartActivity.db.getEvent(eventKey);
            ActionBar.Tab eventTab = bar.newTab();
            eventTab.setTag(event.getEventKey());
            eventTab.setText(event.getShortName());
            eventTab.setTabListener(this);
            bar.addTab(eventTab);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_team, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void setTeam(String key){
        teamKey = key;
        teamNumber = Integer.parseInt(key.substring(3));
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
       getFragmentManager().beginTransaction().replace(R.id.team_view, new EventFragment((String) tab.getTag())).commit();
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    public static class EventFragment extends Fragment{

        private static String eventKey;
        private static View thisView;

        public EventFragment(String key){
            super();
            eventKey = key;
        }

        public EventFragment(){

        }

        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View v = inflater.inflate(R.layout.fragment_event_tab, null);
            thisView = v;
            Button addNote = (Button)v.findViewById(R.id.submit_general_note);
            addNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Note newNote = new Note();
                    newNote.setTeamKey(teamKey);
                    newNote.setEventKey(eventKey);
                    newNote.setMatchKey("all");
                    String noteText = ((EditText)v.findViewById(R.id.new_general_note)).getText().toString();
                    newNote.setNote(noteText);

                    String resultToast;
                    if(StartActivity.db.addNote(newNote) != -1){
                        resultToast = "Note added sucessfully";
                    }else{
                        resultToast = "Error adding note to database";
                    }
                    Toast.makeText(context,resultToast,Toast.LENGTH_SHORT).show();
                    LinearLayout eventList = (LinearLayout) thisView.findViewById(R.id.general_notes);

                    LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);JsonElement element;
                    addNote(newNote,eventList,lparams);
                    EditText addBox = (EditText)v.findViewById(R.id.new_general_note);
                    addBox.setText("");
                }
            });
            fetchNotes();
            return v;
        }

        protected static void fetchNotes(){
            ArrayList<Note> generalNotes = StartActivity.db.getAllNotes(teamKey,eventKey,"all");
            ArrayList<Note> matchNotes = StartActivity.db.getAllMatchNotes(teamKey,eventKey);

            LinearLayout generalList = (LinearLayout) thisView.findViewById(R.id.general_notes);
            LinearLayout matchNotesList = (LinearLayout)thisView.findViewById(R.id.match_notes);

            if(generalNotes.size()>0)
                generalList.removeAllViews();

            for(Note note:generalNotes){
                addNote(note,generalList,Constants.lparams);
            }

            if(matchNotes.size()>0)
                matchNotesList.removeAllViews();

            for(Note note:matchNotes){
                addNote(note,matchNotesList,Constants.lparams);
            }
        }

        private static void addNote(Note note,LinearLayout layout,LinearLayout.LayoutParams params){
            final TextView tv=new TextView(context);
            tv.setLayoutParams(params);
            tv.setText("• " + note.getNote());
            tv.setTextSize(20);
            tv.setLongClickable(true);
            tv.setTag(note.getId());
            tv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    final Note oldNote = StartActivity.db.getNote((Short) tv.getTag());
                    final EditText noteEditField = new EditText(context);
                    //noteEditField.setId(999);
                    noteEditField.setText(oldNote.getNote());

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Note on Team " + teamNumber);
                    builder.setView(noteEditField);
                    builder.setMessage("Edit your note.");
                    builder.setPositiveButton("Update",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    oldNote.setNote(noteEditField.getText().toString());
                                    StartActivity.db.updateNote(oldNote);
                                    fetchNotes();
                                    dialog.cancel();
                                }
                            });

                    builder.setNeutralButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    builder.setNegativeButton("Delete",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    StartActivity.db.deleteNote(oldNote);
                                    fetchNotes();
                                    dialog.cancel();
                                }
                            });
                    builder.create().show();
                    return false;
                }
            });
            layout.addView(tv);
        }

    }
}
