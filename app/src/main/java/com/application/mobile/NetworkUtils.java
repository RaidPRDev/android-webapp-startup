package com.application.mobile;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Rafael Alvarado Emmanuelli on 3/8/2018.
 * WebView App version 1.0.0
 *
 */

public class NetworkUtils implements Runnable
{
    @Override
    public void run() {
        /*
         * Code you want to run on the thread goes here
         */
    }

    public boolean hasActiveInternetConnection(Context context) {
        if (isNetworkAvailable(context))
        {
            try {

                Log.d("MAWV",  "hasActiveInternetConnection()");

                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.amazon.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 200);
            } catch (IOException e) {
                Log.e("MAWV", "Error checking internet connection", e);
            }
        } else {
            Log.d("MAWV", "No network available!");
        }
        return false;
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

}
