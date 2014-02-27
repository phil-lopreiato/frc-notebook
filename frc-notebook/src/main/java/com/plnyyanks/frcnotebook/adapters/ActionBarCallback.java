package com.plnyyanks.frcnotebook.adapters;

import android.app.ActionBar;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.plnyyanks.frcnotebook.R;

/**
 * Created by phil on 2/26/14.
 */
public abstract class ActionBarCallback implements ActionMode.Callback{

    int action_bar_layout;

    public ActionBarCallback(){
        action_bar_layout = R.menu.context_main;
    }

    public ActionBarCallback(int bar_layout){
        action_bar_layout=bar_layout;
    }

    // called when the action mode is created; startActionMode() was called
    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        // Inflate a menu resource providing context menu items
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(action_bar_layout, menu);
        return true;
    }

    // the following method is called each time
    // the action mode is shown. Always called after
    // onCreateActionMode, but
    // may be called multiple times if the mode is invalidated.
    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false; // Return false if nothing is done
    }

    // called when the user selects a contextual menu item
    @Override
    public abstract boolean onActionItemClicked(ActionMode mode, MenuItem item);

    @Override
    public abstract void onDestroyActionMode(ActionMode actionMode);
}
