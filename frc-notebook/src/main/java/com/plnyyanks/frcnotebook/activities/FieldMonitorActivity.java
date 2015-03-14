package com.plnyyanks.frcnotebook.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.plnyyanks.frcnotebook.R;

/**
 * File created by phil on 3/1/14.
 * Copyright 2015, Phil Lopreiato
 * This file is part of FRC Notebook
 * FRC Notebook is licensed under the MIT License
 * (http://opensource.org/licenses/MIT)
 */
public class FieldMonitorActivity extends Activity {

    WebView webview;
    ProgressBar loadProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_monitor);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        webview = (WebView)findViewById(R.id.field_monitor_webview);
        loadProgress = (ProgressBar)findViewById(R.id.webview_progress);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setLoadWithOverviewMode(true); // load fully zoomed out
        webview.getSettings().setBuiltInZoomControls(true);
        webview.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress)
            {
                if(progress < 100 && loadProgress.getVisibility() == ProgressBar.GONE){
                    loadProgress.setVisibility(ProgressBar.VISIBLE);
                    loadProgress.setVisibility(View.VISIBLE);
                }
                loadProgress.setProgress(progress);
                if(progress == 100) {
                    loadProgress.setVisibility(ProgressBar.GONE);
                    loadProgress.setVisibility(View.GONE);
                }
            }
        });
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        webview.loadUrl("http://10.0.100.5/FieldMonitor");
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN){
            switch(keyCode){
                case KeyEvent.KEYCODE_BACK:
                    if(webview.canGoBack()){
                        webview.goBack();
                    }else{
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.field_monitor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id){
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_refresh_field_monitor:
                if(webview!=null){
                    webview.reload();
                }
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
