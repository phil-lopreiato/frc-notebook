package com.plnyyanks.frcnotebook.background.social;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.appspot.frc_notebook_dev.frcNotebookMobile.FrcNotebookMobile;
import com.appspot.frc_notebook_dev.frcNotebookMobile.model.ModelsMobileMessagesBaseResponse;
import com.appspot.frc_notebook_dev.frcNotebookMobile.model.ModelsMobileMessagesCollaboratorAdd;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.plnyyanks.frcnotebook.Constants;
import com.plnyyanks.frcnotebook.helpers.AccountHelper;

import java.io.IOException;

/**
 * Created by phil on 2/22/15.
 */
public class AddCollaborator extends AsyncTask<String, Void, Boolean> {
    private Activity activity;
    
    public AddCollaborator(Activity activity){
        this.activity = activity;
    }
    
    @Override
    protected Boolean doInBackground(String... params) {
        String eventKey = params[0];
        String userEmail = params[1];
        
        GoogleAccountCredential currentCredential = AccountHelper.getSelectedAccountCredential(activity);
        try {
            String token = currentCredential.getToken();
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, "IO Exception while fetching account token for " + currentCredential.getSelectedAccountName());
            e.printStackTrace();
        } catch (GoogleAuthException e) {
            Log.e(Constants.LOG_TAG, "Auth exception while fetching token for "+currentCredential.getSelectedAccountName());
            e.printStackTrace();
        }
        FrcNotebookMobile service = AccountHelper.getMobileApi(currentCredential);
        ModelsMobileMessagesCollaboratorAdd message = new ModelsMobileMessagesCollaboratorAdd();
        message.setEmail(userEmail);
        message.setEventKey(eventKey);
        try {
            ModelsMobileMessagesBaseResponse response = service.collaboratorAdd(message).execute();
            if(response.getCode() == 200 || response.getCode() == 304){
                return true;
            }else{
                Log.e(Constants.LOG_TAG, response.getCode()+":"+response.getData());
                return false;
            }
        } catch (IOException e) {
            Log.w(Constants.LOG_TAG, "Error with adding collaborator");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        String toast;
        if(result){
            Log.d(Constants.LOG_TAG, "Collaboration added");
            toast = "Collaboration successful";
        }else{
            toast = "Error adding collaboration";
        }
        Toast.makeText(activity, toast, Toast.LENGTH_SHORT).show();
    }
}
