package com.plnyyanks.frcnotebook.activities;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.adapters.ListViewArrayAdapter;
import com.plnyyanks.frcnotebook.database.PreferenceHandler;
import com.plnyyanks.frcnotebook.datatypes.ListElement;
import com.plnyyanks.frcnotebook.datatypes.ListItem;
import com.plnyyanks.frcnotebook.dialogs.AddPredefNoteDialog;
import com.plnyyanks.frcnotebook.dialogs.EditPredefNoteDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class PredefinedNoteManager extends ListActivity {

    private ArrayList<ListItem> predef_notes;
    private ArrayList<String> predef_ids;
    private ListViewArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(PreferenceHandler.getTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predefined_note_manager);

        HashMap<Short,String> allPredefNotes = StartActivity.db.getAllDefNotes();
        predef_notes = new ArrayList<ListItem>();
        predef_ids = new ArrayList<String>();

        Iterator<Short> iterator = allPredefNotes.keySet().iterator();
        Short key;
        while(iterator.hasNext()){
            key = iterator.next();
            predef_ids.add(Short.toString(key));
            predef_notes.add(new ListElement(allPredefNotes.get(key),Short.toString(key)));
        }

        adapter = new ListViewArrayAdapter(this,predef_notes,predef_ids);
        setListAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.predefined_note_manager, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id){
            case R.id.action_new_predef_note:
                new AddPredefNoteDialog(adapter).show(getFragmentManager(),"add_predef_note");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        new EditPredefNoteDialog(adapter,position).show(getFragmentManager(),"edit_predef_note");
        super.onListItemClick(l, v, position, id);
    }

}
