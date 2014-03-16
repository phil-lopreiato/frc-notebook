package com.plnyyanks.frcnotebook.activities;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.plnyyanks.frcnotebook.Constants;
import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.adapters.ActionBarCallback;
import com.plnyyanks.frcnotebook.adapters.ListViewArrayAdapter;
import com.plnyyanks.frcnotebook.background.DeleteEvent;
import com.plnyyanks.frcnotebook.database.PreferenceHandler;
import com.plnyyanks.frcnotebook.datatypes.ListElement;
import com.plnyyanks.frcnotebook.datatypes.ListHeader;
import com.plnyyanks.frcnotebook.datatypes.ListItem;
import com.plnyyanks.frcnotebook.dialogs.AddPredefNoteDialog;
import com.plnyyanks.frcnotebook.dialogs.DeleteDialog;
import com.plnyyanks.frcnotebook.dialogs.EditPredefNoteDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class PredefinedNoteManager extends ListActivity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {

    private ArrayList<ListItem> predef_notes;
    private ArrayList<String> predef_ids;
    private ListViewArrayAdapter adapter;
    private Object mActionMode;
    private int selectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(PreferenceHandler.getTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predefined_note_manager);

        HashMap<Short,String> allPredefNotes = StartActivity.db.getAllDefNotes();
        predef_notes = new ArrayList<ListItem>();
        predef_ids = new ArrayList<String>();

        if(allPredefNotes.size()==0){
            predef_ids.add("-1");
            predef_notes.add(new ListElement(getString(R.string.no_predef_notes),"-1"));
        }

        Iterator<Short> iterator = allPredefNotes.keySet().iterator();
        Short key;
        while(iterator.hasNext()){
            key = iterator.next();
            predef_ids.add(Short.toString(key));
            predef_notes.add(new ListElement(allPredefNotes.get(key),Short.toString(key)));
        }

        adapter = new ListViewArrayAdapter(this,predef_notes,predef_ids);
        setListAdapter(adapter);
        getListView().setOnItemLongClickListener(this);
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
        if(adapter.keys.get(position).equals("-1")) return;
        new EditPredefNoteDialog(adapter,position).show(getFragmentManager(),"edit_predef_note");
        super.onListItemClick(l, v, position, id);
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d(Constants.LOG_TAG, "Item Long Click: " + i);
        ListItem item =  adapter.values.get(i);
        if(item instanceof ListHeader) return false;
        if(adapter.keys.get(i).equals("-1")) return false;

        getListView().setOnItemClickListener(null);
        item.setSelected(true);
        view.setSelected(true);
        adapter.notifyDataSetChanged();
        selectedItem = i;
        // start the CAB using the ActionMode.Callback defined above
        mActionMode = startActionMode(mActionModeCallback);
        return false;
    }

    private ActionMode.Callback mActionModeCallback = new ActionBarCallback() {
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    confirmAndDelete(selectedItem);
                    // the Action was executed, close the CAB
                    selectedItem = -1;
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            Log.d(Constants.LOG_TAG,"Destroy CAB");
            mActionMode = null;
            getListView().setOnItemClickListener(PredefinedNoteManager.this);
            getListView().requestFocusFromTouch();
            getListView().clearChoices();
            adapter.notifyDataSetChanged();
        }

        private void confirmAndDelete(final int item){
            DialogInterface.OnClickListener deleter =
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //delete the event now
                            StartActivity.db.deleteDefNote(Short.parseShort(adapter.keys.get(item)));
                            adapter.removeAt(item);
                            adapter.notifyDataSetChanged();
                            dialog.cancel();
                        }
                    };
            new DeleteDialog(getString(R.string.delete_predef_note_message),deleter).show(getFragmentManager(),"delete_predef_note");
        }
    };

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        onListItemClick(getListView(),view,i,l);
    }
}
