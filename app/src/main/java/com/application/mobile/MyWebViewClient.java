package com.application.mobile;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.util.Log;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Rafael Alvarado Emmanuelli on 3/8/2018.
 * WebView App version 1.0.0
 *
 */
public class MyWebViewClient extends WebViewClient
{
    private MainActivity mainActivity;
    private Resources resources;
    private String[] disAllowUrlIndexList;

    public MyWebViewClient(MainActivity activity)
    {
        mainActivity = activity;

        // get disallow url list from config.xml
        resources = mainActivity.getResources();
        disAllowUrlIndexList = resources.getStringArray( R.array.disallow_url_list ) ;
    }

    // on older versions of android, they use
    // shouldOverrideUrlLoading(WebView view, String url)
    // which is deprecated as of SDK 23
    // For SDK 23 devices we use the newer:
    // shouldOverrideUrlLoading(WebView view, WebResourceRequest request)

    @SuppressWarnings("deprecation")
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url)
    {
        if (Build.VERSION.SDK_INT <= 23)
        {
            if (mainActivity.isNetworkAvailable()) checkUrl(view, url);
            else
            {
                mainActivity.showNoInternetAlert(url);
            }

            return false;
        }

        return super.shouldOverrideUrlLoading(view, url);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request)
    {
        if (Build.VERSION.SDK_INT >= 24)
        {
            if (mainActivity.isNetworkAvailable()) checkUrl(view, request.getUrl().toString());
            else
            {
                mainActivity.showNoInternetAlert(request.getUrl().toString());
            }

            return false;
        }

        return super.shouldOverrideUrlLoading(view, request);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon)
    {
        mainActivity.onPageStart();

        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url)
    {
        mainActivity.onPageFinished();

        super.onPageFinished(view, url);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
    {
        if (Build.VERSION.SDK_INT <= 22)
        {
            view.loadData("<HTML><BODY></BODY></HTML>","text/html","utf-8");
        }

        super.onReceivedError(view, errorCode, description, failingUrl);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error)
    {
        if (Build.VERSION.SDK_INT >= 23)
        {
            view.loadData("<HTML><BODY></BODY></HTML>","text/html","utf-8");
        }

        super.onReceivedError(view, request, error);
    }

    @Override
    public void onFormResubmission(WebView view, Message dontResend, Message resend)
    {
        super.onFormResubmission(view, dontResend, resend);
    }

    private void checkUrl(WebView view, String url)
    {
        Log.d(MainActivity.TAG, MyWebViewClient.class.getSimpleName() + ".checkUrl().url: " + url);

        // here we can handle specific urls or phrases, that we don't want loaded
        // in the web view.  Instead we want it to open in the browser

        for (String itemUrlIndex : disAllowUrlIndexList)
        {
            // check if requeted url matches the itemUrlIndex
            if (url.indexOf(itemUrlIndex) != -1)
            {
                Log.d(MainActivity.TAG, "DISALLOWED URLINDEX DETECTED: " + itemUrlIndex);

                view.reload();
                openUrlInBrowser(url);

                // stop iteration
                break;
            }
        }
    }

    private void openUrlInBrowser(String url)
    {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        mainActivity.startActivity(browserIntent);
    }
}