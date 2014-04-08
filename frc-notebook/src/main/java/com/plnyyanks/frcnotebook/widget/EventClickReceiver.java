package com.plnyyanks.frcnotebook.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.plnyyanks.frcnotebook.Constants;
import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.activities.ViewEvent;

/**
 * File created by phil on 4/7/14.
 * Copyright 2014, Phil Lopreiato
 * This file is part of FRC Notebook.
 * FRC Notebook is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * FRC Notebook is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with FRC Notebook. If not, see http://www.gnu.org/licenses/.
 */
public class EventClickReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(Constants.LOG_TAG, "Received Intent: " + intent.getAction());
        if(intent.getAction().equals(context.getString(R.string.action_view_event))){
            ViewEvent.setEvent(intent.getStringExtra("event_key"));
            context.startActivity(new Intent(context,ViewEvent.class));
        }
    }
}
