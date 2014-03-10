package com.plnyyanks.frcnotebook.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
        int themeId = R.style.theme_light;
        if(prefs==null) return themeId;
        String theme = prefs.getString("theme","theme_light");
        if(theme.equals("theme_light")) themeId = R.style.theme_light;
        if(theme.equals("theme_dark")) themeId = R.style.theme_dark;
        return themeId;
    }

    public static void setAppVersion(Context context){
        if(prefs==null)
            prefs = PreferenceManager.getDefaultSharedPreferences(StartActivity.startActivityContext);

        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            prefs.edit().putString("app_version",info.versionName).commit();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
