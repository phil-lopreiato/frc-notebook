package com.plnyyanks.frcnotebook.database;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.plnyyanks.frcnotebook.Constants;
import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.activities.StartActivity;
import com.plnyyanks.frcnotebook.dialogs.DatabaseProgressDialog;
import com.plnyyanks.frcnotebook.json.JSONManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by phil on 3/15/14.
 */
public class ImportDatabase extends AsyncTask<String, String, String> {
    Activity activity;
    DatabaseProgressDialog progress;

    public ImportDatabase(Activity activity) {
        super();
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progress = new DatabaseProgressDialog("Importing Database");
        progress.show(activity.getFragmentManager(), "importing_data");
    }

    @Override
    protected String doInBackground(String... strings) {
        File dbFile = new File(activity.getFilesDir() + "/" + Constants.DB_BACKUP_NAME);
        BufferedReader br = null;
        String contents = "";
        try {

            String sCurrentLine;
            br = new BufferedReader(new FileReader(dbFile));

            while ((sCurrentLine = br.readLine()) != null) {
                contents += sCurrentLine;
            }

        } catch(FileNotFoundException e){
            return "error";
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        System.out.println(contents);
        JsonObject o = JSONManager.getAsJsonObject(contents);
        StartActivity.db.importDatabase(o);
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        progress.dismiss();

        if(s.equals("error")){
            Toast.makeText(activity,activity.getString(R.string.info_no_backup_found),Toast.LENGTH_SHORT).show();
        }
    }
}
