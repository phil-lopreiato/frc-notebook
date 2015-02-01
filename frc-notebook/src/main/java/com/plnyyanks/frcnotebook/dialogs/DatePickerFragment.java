package com.plnyyanks.frcnotebook.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.plnyyanks.frcnotebook.Constants;

import java.util.Arrays;
import java.util.Calendar;

/**
 * File created by phil on 4/15/14.
 * Copyright 2015, Phil Lopreiato
 * This file is part of FRC Notebook
 * FRC Notebook is licensed under the MIT License
 * (http://opensource.org/licenses/MIT)
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private Button parentView;

    public DatePickerFragment(){

    }

    public DatePickerFragment(View v){
        parentView = (Button)v;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        int year,month,day;

        if(parentView == null || parentView.getText().toString().split("-").length!=3) {
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }else{
            String[] split = parentView.getText().toString().split("-");
            Log.d(Constants.LOG_TAG, Arrays.toString(split));
            month = Integer.parseInt(split[1]);
            day = Integer.parseInt(split[2]);
            year = Integer.parseInt(split[0]);
        }

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        if(parentView == null) return;

        parentView.setText(year+"-"+(month+1)+"-"+day);
    }
}
