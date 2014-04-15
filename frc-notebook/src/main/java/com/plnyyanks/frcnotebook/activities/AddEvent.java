package com.plnyyanks.frcnotebook.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.plnyyanks.frcnotebook.Constants;
import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.background.ShowLocalEvents;
import com.plnyyanks.frcnotebook.database.PreferenceHandler;
import com.plnyyanks.frcnotebook.dialogs.DatePickerFragment;

public class AddEvent extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(PreferenceHandler.getTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
    }

    @Override
    protected void onResume() {
        StartActivity.checkThemeChanged(AddEvent.class);
        super.onResume();
    }

    public void showDatePicker(View v){
        new DatePickerFragment(v).show(getFragmentManager(),"datePicker");
    }

    public void getScheduleFile(View v){
        // in onCreate or any event where your want the user to
        // select a file
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), Constants.SELECT_FILE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == Constants.SELECT_FILE_REQUEST_CODE){
            Uri selectedFile = data.getData();
            Toast.makeText(this,selectedFile.getPath(),Toast.LENGTH_SHORT).show();
            if(validateScheduleFileType(selectedFile.getPath())){
                TextView filePicker = (TextView) findViewById(R.id.match_schedule);
                filePicker.setText(selectedFile.getPathSegments().get(selectedFile.getPathSegments().size()-1));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean validateScheduleFileType(String file){
        final String[] validExtensions = {"csv"};
        boolean result = false;
        for(String ext:validExtensions){
            result = result || file.matches("\\w+\\."+ext+"$");
            
        }
        return result;
    }
}
