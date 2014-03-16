package com.plnyyanks.frcnotebook.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;

import com.plnyyanks.frcnotebook.activities.StartActivity;
import com.plnyyanks.frcnotebook.adapters.AllianceExpandableListAdapter;
import com.plnyyanks.frcnotebook.adapters.ListViewArrayAdapter;
import com.plnyyanks.frcnotebook.adapters.NotesExpandableListAdapter;
import com.plnyyanks.frcnotebook.background.GetNotesForMatch;
import com.plnyyanks.frcnotebook.background.GetNotesForTeam;
import com.plnyyanks.frcnotebook.datatypes.ListElement;

/**
 * Created by phil on 3/15/14.
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
