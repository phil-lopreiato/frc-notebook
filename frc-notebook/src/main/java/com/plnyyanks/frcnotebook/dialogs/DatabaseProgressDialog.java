package com.plnyyanks.frcnotebook.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;

/**
 * Created by phil on 3/15/14.
 */
public class DatabaseProgressDialog extends DialogFragment {

    private static String msg;

    public DatabaseProgressDialog(String message){
        this();
        msg = message;
    }

    public DatabaseProgressDialog(){
        super();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ProgressDialog dialog = ProgressDialog.show(getActivity(), "Please wait ...", msg, true);
        dialog.setCancelable(false);
        return dialog;
    }
}
