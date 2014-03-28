package com.plnyyanks.frcnotebook.background;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.plnyyanks.frcnotebook.activities.StartActivity;

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
