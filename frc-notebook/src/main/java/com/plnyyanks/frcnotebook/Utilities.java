package com.plnyyanks.frcnotebook;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by phil on 2/14/15.
 */
public class Utilities {
    public static String readLocalProperty(Context c, String property) {
        Properties properties;
        properties = new Properties();
        try {
            InputStream fileStream = c.getAssets().open("frcnotebook.properties");
            properties.load(fileStream);
            fileStream.close();
            if(isDebuggable() && properties.containsKey(property + ".debug")){
                return properties.getProperty(property + ".debug");
            }
            return properties.getProperty(property, "");
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, "Unable to read from tba.properties");
            e.printStackTrace();
        }
        return "";
    }

    public static boolean isDebuggable() {
        return BuildConfig.DEBUG;
    }

    /**
     * Get the <a href="http://developer.android.com/reference/android/os/Build.html#SERIAL">hardware serial number</a>
     * I hope this actually works universally, android UUIDs are irritatingly difficult
     *
     * @return UUID
     */
    public static String getUUID(Context context) {
        return Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
