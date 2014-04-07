package com.plnyyanks.frcnotebook.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.activities.StartActivity;
import com.plnyyanks.frcnotebook.adapters.ListViewArrayAdapter;
import com.plnyyanks.frcnotebook.datatypes.Event;
import com.plnyyanks.frcnotebook.datatypes.ListElement;
import com.plnyyanks.frcnotebook.datatypes.ListItem;

import java.util.ArrayList;

/**
 * File created by phil on 4/7/14.
 * Copyright 2014, Phil Lopreiato
 * This file is part of FRC Notebook.
 * FRC Notebook is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * FRC Notebook is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with FRC Notebook. If not, see http://www.gnu.org/licenses/.
 */
public class WidgetProvider extends AppWidgetProvider{
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int i=0; i<appWidgetIds.length; i++) {
            ArrayList<Event> events = StartActivity.db.getUpcomingEvents();
            ListItem[] finalEvents = new ListItem[events.size()];
            String[] finalKeys = new String[events.size()];
            for (int j = 0; j < finalEvents.length; j++) {
                finalEvents[j] = new ListElement(events.get(j).getEventName(), events.get(j).getEventKey());
                finalKeys[j] = events.get(j).getEventKey();
            }

            // Create an Intent to launch ExampleActivity
            Intent intent = new Intent(context, StartActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);


            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            ListViewArrayAdapter adapter = new ListViewArrayAdapter(context, finalEvents, finalKeys);
            views.setOnClickPendingIntent(R.id.widget_title, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetIds[i],views);
        }
    }
}
