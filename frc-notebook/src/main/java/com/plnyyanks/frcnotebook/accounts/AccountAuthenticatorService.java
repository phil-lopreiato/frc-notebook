package com.plnyyanks.frcnotebook.accounts;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by phil on 2/14/15.
 */
public class AccountAuthenticatorService extends Service{
    @Override
    public IBinder onBind(Intent intent) {
        AccountAuthenticator authenticator = new AccountAuthenticator(this);
        return authenticator.getIBinder();
    }
}
