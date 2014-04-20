package com.plnyyanks.frcnotebook.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;

import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.activities.StartActivity;
import com.plnyyanks.frcnotebook.adapters.AdapterInterface;
import com.plnyyanks.frcnotebook.datatypes.Note;

/**
 * File created by phil on 3/15/14.
 * Copyright 2014, Phil Lopreiato
 * This file is part of FRC Notebook.
 * FRC Notebook is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * FRC Notebook is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with FRC Notebook. If not, see http://www.gnu.org/licenses/.
 */
public class EditNoteDialog extends DialogFragment {

    private String title;
    private Note oldNote;
    private short noteId;
    private AdapterInterface adapter;

    public EditNoteDialog(){
        super();
    }

    public EditNoteDialog(String title,Note current, short id,AdapterInterface ad){
        this();
        this.title = title;
        oldNote = current;
        noteId = id;
        adapter = ad;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final EditText noteEditField = new EditText(getActivity());
        //noteEditField.setId(999);
        noteEditField.setText(noteId!=-1?oldNote.getNote():"");
        if(oldNote.getParent()!= -1) {
            //note is a derivative of a predefined note. Don't allow editing
            noteEditField.setEnabled(false);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setView(noteEditField);
        builder.setPositiveButton(getString(R.string.submit),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        oldNote.setNote(noteEditField.getText().toString());
                        if(noteId==-1){
                            short newId = StartActivity.db.addNote(oldNote);
                            adapter.addNote(StartActivity.db.getNote(newId));
                        }else{
                            StartActivity.db.updateNote(oldNote);
                            adapter.updateNote(oldNote);
                        }
                        adapter.updateListData();
                        dialog.cancel();
                    }
                }
        );

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
