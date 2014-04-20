package com.plnyyanks.frcnotebook.background;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.plnyyanks.frcnotebook.activities.StartActivity;

/**
 * File created by phil on 3/1/14.
 * Copyright 2014, Phil Lopreiato
 * This file is part of FRC Notebook.
 * FRC Notebook is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * FRC Notebook is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with FRC Notebook. If not, see http://www.gnu.org/licenses/.
 */
public class DeleteEvent extends AsyncTask<String,String,String> {

    private Context context;
    private String eventKey;

    public DeleteEvent(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... strings) {
        eventKey = strings[0];
        StartActivity.db.deleteEvent(eventKey);

        return "";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Toast.makeText(context, "Deleted " + eventKey + " from database", Toast.LENGTH_SHORT).show();
    }
}
