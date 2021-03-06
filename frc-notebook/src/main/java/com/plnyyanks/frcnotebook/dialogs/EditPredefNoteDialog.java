package com.plnyyanks.frcnotebook.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;

import com.plnyyanks.frcnotebook.activities.StartActivity;
import com.plnyyanks.frcnotebook.adapters.ListViewArrayAdapter;
import com.plnyyanks.frcnotebook.datatypes.ListElement;

/**
 * File created by phil on 3/15/14.
 * Copyright 2015, Phil Lopreiato
 * This file is part of FRC Notebook
 * FRC Notebook is licensed under the MIT License
 * (http://opensource.org/licenses/MIT)
 */
public class EditPredefNoteDialog extends DialogFragment {

    static ListViewArrayAdapter adapter;
    static int position;

    public EditPredefNoteDialog(){

    }

    public EditPredefNoteDialog(ListViewArrayAdapter a, int pos){
        this();
        adapter = a;
        position = pos;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final EditText editField = new EditText(getActivity());
        editField.setText(StartActivity.db.getDefNote(Short.parseShort(adapter.keys.get(position))));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Edit Predefined Note");
        builder.setView(editField);
        builder.setPositiveButton("Submit",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String text = editField.getText().toString();
                        if(!text.equals("")) {
                            StartActivity.db.updateDefNote(Short.parseShort(adapter.keys.get(position)),text);
                            adapter.values.set(position,new ListElement(text,adapter.keys.get(position)));
                            adapter.notifyDataSetChanged();
                        }
                        dialog.cancel();

                    }
                }
        );

        builder.setNeutralButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }
        );
        return builder.create();
    }
}
