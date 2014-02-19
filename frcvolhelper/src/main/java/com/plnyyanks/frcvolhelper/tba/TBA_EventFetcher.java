package com.plnyyanks.frcvolhelper.tba;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.plnyyanks.frcvolhelper.R;
import com.plnyyanks.frcvolhelper.json.JSONManager;

import java.util.Iterator;

/**
 * Created by phil on 2/18/14.
 */
public class TBA_EventFetcher extends AsyncTask<Activity,String,JsonArray>{

    private Activity listActivity;

    @Override
    protected JsonArray doInBackground(Activity... args) {
        listActivity = args[0];
        String data = GET_Request.getWebData("http://www.thebluealliance.com/api/v1/events/list?year=2014");
        return JSONManager.getasJsonArray(data);
    }

    @Override
    protected void onPostExecute(JsonArray result) {
        super.onPostExecute(result);
        //Log.d(Constants.LOG_TAG,"Event Data: "+result.toString());

        //now, add the events to the event picker activity
        Iterator<JsonElement> iterator = result.iterator();

        LinearLayout eventList = (LinearLayout) listActivity.findViewById(R.id.event_list_to_download);

        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        JsonElement element;
        String eventName;
        while(iterator.hasNext()){
            element = iterator.next();
            eventName = element.getAsJsonObject().get("name").toString();
            TextView tv=new TextView(listActivity);
            tv.setLayoutParams(lparams);
            tv.setText("• " + eventName.substring(1, eventName.length() - 1));
            tv.setTextSize(20);
            tv.setClickable(true);
            tv.setTag(element.getAsJsonObject().get("key").toString());
            tv.setOnClickListener(new EventClickListener());
            eventList.addView(tv);
        }

        //hide the progress bar
        ProgressBar prog = (ProgressBar) listActivity.findViewById(R.id.event_loading_progress);
        prog.setVisibility(View.GONE);
    }

    private class EventClickListener implements View.OnClickListener {

        String key;

        @Override
        public void onClick(View view) {
            key = ((String)view.getTag()).replaceAll("^\"|\"$", "");

            AlertDialog.Builder builder = new AlertDialog.Builder(listActivity);
            DialogInterface.OnClickListener dialogClickListener = new DialogClickListener();

            builder.setMessage("Do you want to download info for "+key+"?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
        }

        private class DialogClickListener implements DialogInterface.OnClickListener {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //YES! Download info...
                        Toast.makeText(listActivity, "Downloading Info for "+key,Toast.LENGTH_SHORT).show();
                        //start the background task to download matches
                        new TBA_EventDetailFetcher(listActivity,key).execute("");
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
    }


}
