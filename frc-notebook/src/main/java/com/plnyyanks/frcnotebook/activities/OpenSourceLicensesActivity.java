package com.plnyyanks.frcnotebook.activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;

import com.plnyyanks.frcnotebook.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by phil on 1/31/15.
 * Copyright 2015, Phil Lopreiato
 * This file is part of FRC Notebook
 * FRC Notebook is licensed under the MIT License
 * (http://opensource.org/licenses/MIT)*
 */
public class OpenSourceLicensesActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_source_licenses);
        TextView text = (TextView) findViewById(R.id.text);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.licenses)));
            try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(System.getProperty("line.separator"));
                    line = br.readLine();
                }
                String everything = sb.toString();
                text.setText(Html.fromHtml(everything));
            } finally {
                br.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            text.setText("Error reading licenses file.");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
