package com.plnyyanks.frcnotebook.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.plnyyanks.frcnotebook.R;
import com.plnyyanks.frcnotebook.database.PreferenceHandler;
import com.plnyyanks.frcnotebook.helpers.AccountHelper;
import com.plnyyanks.frcnotebook.helpers.PlusHelper;

/**
 * Created by phil on 2/14/15.
 */
public class AuthenticatorActivity extends PlusBaseActivity {

    private static final String ACTIVITY_FIRST_TIME = "first_time";

    // UI references.
    private View mProgressView;
    private SignInButton mPlusSignInButton;
    private Button mPlusNotNowButton;
    private View mLoginFormView;

    public static Intent newInstance(Context context, boolean firstTime) {
        Intent intent = new Intent(context, AuthenticatorActivity.class);
        intent.putExtra(ACTIVITY_FIRST_TIME, firstTime);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticator);

        boolean firstTime;
        if (getIntent() != null) {
            firstTime = getIntent().getBooleanExtra(ACTIVITY_FIRST_TIME, true);
        } else {
            firstTime = true;
        }

        // Find the Google+ sign in button.
        mPlusSignInButton = (SignInButton) findViewById(R.id.plus_sign_in_button);
        mPlusNotNowButton = (Button) findViewById(R.id.cloud_opt_out_button);
        if (supportsGooglePlayServices()) {
            // Set a listener to connect the user when the G+ button is clicked.
            mPlusSignInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    signIn();
                }
            });
        } else {
            // Don't offer G+ sign in if the app's version is too low to support Google Play
            // Services.
            mPlusSignInButton.setVisibility(View.GONE);
            TextView desc = (TextView) findViewById(R.id.mytba_message);
            desc.setText(getString(R.string.gms_not_supported));
            return;
        }

        final Intent homeIntent = StartActivity.newInstance(this);
        final Activity activity = this;
        mPlusNotNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceHandler.setHasLoaded(true);
                AccountHelper.enableCloud(activity, false);
                startActivity(homeIntent);
            }
        });

        if (!firstTime) {
            mPlusNotNowButton.setText(getString(R.string.not_now));
        }

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.INVISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    protected void onPlusClientSignIn() {
        PreferenceHandler.setHasLoaded(true);
        AccountHelper.enableCloud(this, true);
        AccountHelper.setSelectedAccount(this, PlusHelper.getAccountName());
        startActivity(StartActivity.newInstance(this));
    }

    @Override
    protected void onPlusClientBlockingUI(boolean show) {
        showProgress(show);
    }

    @Override
    protected void updateConnectButtonState() {
        boolean connected = PlusHelper.isConnected();

        mPlusSignInButton.setVisibility(connected ? View.GONE : View.VISIBLE);
        mPlusNotNowButton.setVisibility(connected ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onPlusClientRevokeAccess() {
        // TODO: Access to the user's G+ account has been revoked.  Per the developer terms, delete
        // any stored user data here.
    }

    @Override
    protected void onPlusClientSignOut() {
        AccountHelper.enableCloud(this, false);
    }

    /**
     * Check if the device supports Google Play Services.  It's best
     * practice to check first rather than handling this as an error case.
     *
     * @return whether the device supports Google Play Services
     */
    private boolean supportsGooglePlayServices() {
        return GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) ==
                ConnectionResult.SUCCESS;
    }
}