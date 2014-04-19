package com.plnyyanks.frcnotebook.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

/**
 * Created by phil on 3/15/14.
 */
public class ProgressDialog extends DialogFragment {

    private static String msg;

    public ProgressDialog(String message){
        this();
        msg = message;
    }

    public ProgressDialog(){
        super();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        android.app.ProgressDialog dialog = android.app.ProgressDialog.show(getActivity(), "Please wait ...", msg, true);
        dialog.setCancelable(false);
        return dialog;
    }
}
