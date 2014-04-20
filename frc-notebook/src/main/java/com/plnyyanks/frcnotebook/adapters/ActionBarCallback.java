package com.plnyyanks.frcnotebook.adapters;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.plnyyanks.frcnotebook.R;

/**
 * File created by phil on 2/26/14.
 * Copyright 2014, Phil Lopreiato
 * This file is part of FRC Notebook.
 * FRC Notebook is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * FRC Notebook is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with FRC Notebook. If not, see http://www.gnu.org/licenses/.
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
