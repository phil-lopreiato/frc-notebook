package com.plnyyanks.frcnotebook.helpers;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.appspot.frc_notebook_dev.frcNotebookMobile.FrcNotebookMobile;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.plnyyanks.frcnotebook.Constants;
import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.Utilities;

import java.io.IOException;

/**
 * Created by phil on 2/14/15.
 */
public class AccountHelper {

    public static final String PREF_MYTBA_ENABLED = "mytba_enabled";
    public static final String PREF_SELECTED_ACCOUNT = "selected_account";

    public static final JsonFactory JSON_FACTORY = new AndroidJsonFactory();
    public static final HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();

    public static void enableCloud(Activity activity, boolean enabled) {
        Log.d(Constants.LOG_TAG, "Enabling myTBA: " + enabled);
        if (enabled) {
            // enable myTBA
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
            prefs.edit().putBoolean(PREF_MYTBA_ENABLED, true).apply();
        } else {
            //disabled myTBA.
            String currentUser = getSelectedAccount(activity);
            if (!currentUser.isEmpty()) {
                Log.d(Constants.LOG_TAG, "removing: " + currentUser);
                //Remove all local content and deregister from GCM
                //TODO deregister from GCM
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
                prefs.edit()
                        .putBoolean(PREF_MYTBA_ENABLED, false).apply();
            }
        }
    }

    public static boolean isCloudEnabled(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(PREF_MYTBA_ENABLED, false);
    }

    public static void setSelectedAccount(Context context, String accoutName) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(PREF_SELECTED_ACCOUNT, accoutName).apply();
    }

    public static String getSelectedAccount(Context context) {
        if(context == null) return "";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(PREF_SELECTED_ACCOUNT, "");
    }

    public static Account getCurrentAccount(Context context){
        AccountManager am = AccountManager.get(context);
        Account[] accounts = am.getAccountsByType(context.getString(R.string.account_type));
        String selectedAccount = getSelectedAccount(context);
        for(Account account: accounts){
            if(account.name.equals(selectedAccount)) return account;
        }
        return null;
    }

    public static boolean isAccountSelected(Context context) {
        return !getSelectedAccount(context).isEmpty();
    }

    public static GoogleAccountCredential getSelectedAccountCredential(Context context) {
        String accountName = getSelectedAccount(context);
        if (accountName == null || accountName.isEmpty()) {
            Log.w(Constants.LOG_TAG, "Can't get credential without selected account");
            return null;
        }

        Log.d(Constants.LOG_TAG, "Getting credential for " + accountName);
        GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(context, getAudience(context));
        credential.setSelectedAccountName(accountName);

        return credential;
    }

    public static FrcNotebookMobile getMobileApi(GoogleAccountCredential credential) {
        FrcNotebookMobile.Builder api = new FrcNotebookMobile.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential);
        api.setApplicationName("The Blue Alliance");
        return api.build();
    }

    public static FrcNotebookMobile getAuthedApi(Context context) {
        GoogleAccountCredential currentCredential = AccountHelper.getSelectedAccountCredential(context);
        if(currentCredential == null){
            Log.w(Constants.LOG_TAG, "Unable to get account.");
            return null;
        }
        try {
            String token = currentCredential.getToken();
            Log.d(Constants.LOG_TAG, "token: " + token);
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, "IO Exception while fetching account token for " + currentCredential.getSelectedAccountName());
            e.printStackTrace();
        } catch (GoogleAuthException e) {
            Log.e(Constants.LOG_TAG, "Auth exception while fetching token for " + currentCredential.getSelectedAccountName());
            e.printStackTrace();
        }
        return AccountHelper.getMobileApi(currentCredential);
    }

    public static String getWebClientId(Context context) {
        return Utilities.readLocalProperty(context, "appspot.webClientId");
    }

    public static String getAndroidClientId(Context context) {
        return Utilities.readLocalProperty(context, "appspot.androidClientId");
    }

    public static String getAudience(Context context) {
        return "server:client_id:" + getWebClientId(context);
    }

    public static int countGoogleAccounts(Context context) {
        AccountManager am = AccountManager.get(context);
        Account[] accounts = am.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
        if (accounts == null || accounts.length < 1) {
            return 0;
        } else {
            return accounts.length;
        }
    }

    public static boolean checkGooglePlayServicesAvailable(Activity activity) {
        final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(activity, connectionStatusCode);
            return false;
        }
        return true;
    }

    public static void showGooglePlayServicesAvailabilityErrorDialog(final Activity activity,
                                                                     final int connectionStatusCode) {
        final int REQUEST_GOOGLE_PLAY_SERVICES = 0;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                        connectionStatusCode, activity, REQUEST_GOOGLE_PLAY_SERVICES);
                dialog.show();
            }
        });
    }
}