package com.plnyyanks.frcnotebook.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.activities.StartActivity;

/**
 * Created by phil on 3/16/14.
 */
public class DeleteDialog extends DialogFragment{

    private static String message;
    private static DialogInterface.OnClickListener delete;

    public DeleteDialog(){
        super();
    }
    public DeleteDialog(String msg,DialogInterface.OnClickListener listener){
        this();
        message = msg;
        delete = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.delete_title));
        builder.setMessage(message);
        builder.setPositiveButton(getString(R.string.yes),delete);

        builder.setNegativeButton(getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }
}
