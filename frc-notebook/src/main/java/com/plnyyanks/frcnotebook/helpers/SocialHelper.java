package com.plnyyanks.frcnotebook.helpers;

import android.app.Activity;

import com.plnyyanks.frcnotebook.background.social.AddCollaborator;

/**
 * Created by phil on 2/22/15.
 */
public class SocialHelper {
    
    public static void addCollaborator(Activity activity, String eventKey, String email){
        new AddCollaborator(activity).execute(eventKey, email);
    }
    
}
