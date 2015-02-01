package com.plnyyanks.frcnotebook.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.activities.StartActivity;
import com.plnyyanks.frcnotebook.background.AddMatchesFromURL;
import com.plnyyanks.frcnotebook.datatypes.Event;

/**
 * File created by phil on 4/19/14.
 * Copyright 2015, Phil Lopreiato
 * This file is part of FRC Notebook
 * FRC Notebook is licensed under the MIT License
 * (http://opensource.org/licenses/MIT)
 */
public class InputURLForMatchesDialog extends DialogFragment {

    private String title;
    private Event event;

    public InputURLForMatchesDialog(){
        title = "";
    }

    public InputURLForMatchesDialog(String title,Event event){
        this.title = title;
        this.event = event;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final EditText urlField = new EditText(getActivity());
        urlField.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setView(urlField);
        builder.setPositiveButton(getString(R.string.submit),new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(event != null && urlField.getText()!=null){
                    new AddMatchesFromURL(getActivity()).execute(urlField.getText().toString(),event.getEventKey());
                }
            }
        });

        builder.setNeutralButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }
        );
        return builder.create();
    }
}
