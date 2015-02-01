package com.plnyyanks.frcnotebook.database;

import android.app.Activity;
import android.os.AsyncTask;

import com.google.gson.JsonObject;
import com.plnyyanks.frcnotebook.Constants;
import com.plnyyanks.frcnotebook.activities.StartActivity;
import com.plnyyanks.frcnotebook.dialogs.ProgressDialog;

import java.io.File;
import java.io.PrintWriter;

/**
 * File created by phil on 3/1/14.
 * Copyright 2015, Phil Lopreiato
 * This file is part of FRC Notebook
 * FRC Notebook is licensed under the MIT License
 * (http://opensource.org/licenses/MIT)
 */
public class BackupDatabase extends AsyncTask<Boolean,String,String> {

    Activity activity;
    ProgressDialog progress;

    public BackupDatabase(Activity activity){
        super();
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progress = new ProgressDialog("Exporting Database");
        progress.show(activity.getFragmentManager(),"exporting_data");
    }

    @Override
    protected String doInBackground(Boolean... bools) {
        JsonObject db = StartActivity.db.exportDatabase(bools[0],bools[1],bools[2],bools[3]);
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
