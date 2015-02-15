package com.plnyyanks.frcnotebook.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import com.appspot.frc_notebook_dev.frcNotebookMobile.FrcNotebookMobile;
import com.appspot.frc_notebook_dev.frcNotebookMobile.model.ModelsMobileMessagesBaseResponse;
import com.appspot.frc_notebook_dev.frcNotebookMobile.model.ModelsMobileMessagesRegistrationRequest;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.plnyyanks.frcnotebook.Constants;
import com.plnyyanks.frcnotebook.Utilities;
import com.plnyyanks.frcnotebook.background.gcm.RegisterGCM;

import java.io.IOException;

/**
 * Created by phil on 2/14/15.
 */
public class GCMAuthHelper {

    public static final String OS_ANDROID = "android";
    public static final String PROPERTY_GCM_REG_ID = "gcm_registration_id";
    public static final String REGISTRATION_CHECKSUM = "checksum";


    public static String getRegistrationId(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(PROPERTY_GCM_REG_ID, "");
    }

    public static void registerInBackground(Activity activity) {
        new RegisterGCM(activity).execute();
    }

    public static void storeRegistrationId(Context context, String id) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(PROPERTY_GCM_REG_ID, id).apply();
    }

    public static boolean sendRegistrationToBackend(Activity activity, String gcmId) {
        Log.i(Constants.LOG_TAG, "Registering gcmId " + gcmId);
        GoogleAccountCredential currentCredential = AccountHelper.getSelectedAccountCredential(activity);
        try {
            String token = currentCredential.getToken();
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, "IO Exception while fetching account token for "+currentCredential.getSelectedAccountName());
            e.printStackTrace();
        } catch (GoogleAuthException e) {
            Log.e(Constants.LOG_TAG, "Auth exception while fetching token for "+currentCredential.getSelectedAccountName());
            e.printStackTrace();
        }

        FrcNotebookMobile service = AccountHelper.getMobileApi(currentCredential);
        ModelsMobileMessagesRegistrationRequest request = new ModelsMobileMessagesRegistrationRequest();
        request.setMobileId(gcmId);
        request.setOperatingSystem(OS_ANDROID);
        request.setName(Build.MANUFACTURER + " " + Build.MODEL);
        request.setDeviceUuid(Utilities.getUUID(activity));
        
        try{
            ModelsMobileMessagesBaseResponse response = service.register(request).execute();
            if(response.getCode() == 200 || response.getCode() == 304){
                return true;
            }else{
                Log.e(Constants.LOG_TAG, response.getCode()+":"+response.getData());
                return false;
            }
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, "Error with API call.");
            e.printStackTrace();
        }

        return false;
    }

}