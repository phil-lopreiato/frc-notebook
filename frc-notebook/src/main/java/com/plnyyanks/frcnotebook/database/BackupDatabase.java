package com.plnyyanks.frcnotebook.database;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonObject;
import com.plnyyanks.frcnotebook.Constants;
import com.plnyyanks.frcnotebook.activities.StartActivity;
import com.plnyyanks.frcnotebook.dialogs.DatabaseProgressDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Created by phil on 3/15/14.
 */
public class BackupDatabase extends AsyncTask<Boolean,String,String> {

    Activity activity;
    DatabaseProgressDialog progress;

    public BackupDatabase(Activity activity){
        super();
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progress = new DatabaseProgressDialog("Exporting Database");
        progress.show(activity.getFragmentManager(),"exporting_data");
    }

    @Override
    protected String doInBackground(Boolean... bools) {
        JsonObject db = StartActivity.db.exportDatabase(bools[0].booleanValue(),bools[1].booleanValue(),bools[2].booleanValue(),bools[3].booleanValue());
        String filename = Constants.DB_BACKUP_NAME;
        File file = new File(activity.getFilesDir(), filename);
        PrintWriter out;
        System.out.println(db.toString());
        try {
            out = new PrintWriter(file);
            out.println(db.toString());
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        progress.dismiss();
    }
}
