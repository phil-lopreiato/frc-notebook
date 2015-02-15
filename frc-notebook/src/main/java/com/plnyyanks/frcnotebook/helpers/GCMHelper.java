package com.plnyyanks.frcnotebook.helpers;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.plnyyanks.frcnotebook.Constants;
import com.plnyyanks.frcnotebook.Utilities;

/**
 * Created by phil on 2/14/15.
 */
public class GCMHelper {

    private static String senderId;

    public static GoogleCloudMessaging getGcm(Context context) {
        return GoogleCloudMessaging.getInstance(context);
    }

    public static String getSenderId(Context c) {
        if (senderId == null) {
            senderId = Utilities.readLocalProperty(c, "gcm.senderId");
        }
        return senderId;
    }

    public static void registerGCMIfNeeded(Activity activity){
        if (!AccountHelper.checkGooglePlayServicesAvailable(activity)) {
            Log.w(Constants.LOG_TAG, "Google Play Services unavailable. Can't register with GCM");
            return;
        }
        final String registrationId = GCMAuthHelper.getRegistrationId(activity);
        if (TextUtils.isEmpty(registrationId)) {
            // GCM has not yet been registered on this device
            Log.d(Constants.LOG_TAG, "GCM is not currently registered. Registering....");
            GCMAuthHelper.registerInBackground(activity);
        }
    }

}