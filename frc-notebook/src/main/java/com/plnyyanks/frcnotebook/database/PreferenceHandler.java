package com.plnyyanks.frcnotebook.database;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.activities.StartActivity;

/**
 * Created by phil on 2/24/14.
 */
public class PreferenceHandler {
    private static SharedPreferences prefs;

    public static int getTheme(){
        if(prefs==null)
            prefs = PreferenceManager.getDefaultSharedPreferences(StartActivity.startActivityContext);

        String theme = prefs.getString("theme","theme_light");
        int themeId = R.style.theme_light;
        if(theme.equals("theme_light")) themeId = R.style.theme_light;
        if(theme.equals("theme_dark")) themeId = R.style.theme_dark;
        return themeId;
    }
}
