package com.plnyyanks.frcnotebook.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.preference.PreferenceManager;

import com.plnyyanks.frcnotebook.Constants;
import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.activities.StartActivity;

import java.util.Calendar;

/**
 * File created by phil on 3/1/14.
 * Copyright 2015, Phil Lopreiato
 * This file is part of FRC Notebook
 * FRC Notebook is licensed under the MIT License
 * (http://opensource.org/licenses/MIT)
 */
public class PreferenceHandler {
    private static SharedPreferences prefs;

    public static int getTheme(){
        int themeId = R.style.theme_light;
        if(StartActivity.startActivityContext==null)return themeId;
        if(prefs==null)
            prefs = PreferenceManager.getDefaultSharedPreferences(StartActivity.startActivityContext);
        if(prefs==null) return themeId;
        String theme = prefs.getString("theme","theme_light");
        if(theme.equals("theme_light")) themeId = R.style.theme_light;
        if(theme.equals("theme_dark")) themeId = R.style.theme_dark;
        return themeId;
    }

    public static boolean getFMEnabled(){
        if(prefs==null)
            prefs = PreferenceManager.getDefaultSharedPreferences(StartActivity.startActivityContext);
        return prefs != null && prefs.getBoolean("show_field_monitor", false);
    }

    public static boolean getTimesEnabled(){
        if(prefs==null)
            prefs = PreferenceManager.getDefaultSharedPreferences(StartActivity.startActivityContext);
        return prefs != null && prefs.getBoolean("show_match_times", false);
    }

    public static String getYear(){
        if(prefs==null)
            prefs = PreferenceManager.getDefaultSharedPreferences(StartActivity.startActivityContext);
        return prefs.getString("competition_season", Integer.toString(Calendar.getInstance().get(Calendar.YEAR)));
    }

    public static boolean showMatchScores(){
        if(prefs==null)
            prefs = PreferenceManager.getDefaultSharedPreferences(StartActivity.startActivityContext);
        return prefs == null || prefs.getBoolean("show_scores", true);
    }

    public static boolean showGeneralNotes(){
        if(prefs==null)
            prefs = PreferenceManager.getDefaultSharedPreferences(StartActivity.startActivityContext);
        return prefs != null && prefs.getBoolean("show_general_notes", false);
    }

    public static void setAppVersion(Context context){
        if(prefs==null)
            prefs = PreferenceManager.getDefaultSharedPreferences(StartActivity.startActivityContext);

        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            if(info.versionCode!= prefs.getInt("version_code",0)); updatePrefs(prefs.getInt("version_code",0),info.versionCode);
            prefs.edit().putString("app_version",info.versionName).putInt("version_code",info.versionCode).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getAppVersion(){
        if(prefs==null)
            prefs = PreferenceManager.getDefaultSharedPreferences(StartActivity.startActivityContext);
        return prefs.getString("app_version","");
    }

    private static void updatePrefs(int old,int current){
        if(old<20 && current >= 20){
            setDataSource(Constants.DATAFEED_SOURCES.TBAv2);
        }
    }

    public static Constants.DATAFEED_SOURCES getDataSource(){
        if(prefs==null)
            prefs = PreferenceManager.getDefaultSharedPreferences(StartActivity.startActivityContext);
        if(prefs == null)
            return Constants.DATAFEED_SOURCES.TBAv2; //if still null...

        return Constants.DATAFEED_SOURCES.valueOf(prefs.getString("data_source", Constants.DATAFEED_SOURCES.TBAv2.toString()));
    }

    public static void setDataSource(Constants.DATAFEED_SOURCES source){
        if(prefs==null)
            prefs = PreferenceManager.getDefaultSharedPreferences(StartActivity.startActivityContext);
        if(prefs == null)
            return;

        prefs.edit().putString("data_source",source.toString()).commit();
    }
}
