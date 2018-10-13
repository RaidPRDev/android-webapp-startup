package com.application.mobile;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.RotateDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.net.NetworkInfo;

/**
 * Created by Rafael Alvarado Emmanuelli on 3/8/2018.
 * WebView App version 1.0.0
 *
 */

public class MainActivity extends AppCompatActivity
{
    // tells us if the app has been loaded completely
    private boolean isAppInitialized = false;
    private boolean isDebug = false;

    // web view and launch image
    private WebView myWebView;

    // url settings, found in : resourses->strings.xml
    public String webViewUrl;               // web_view_url

    private ImageView myLaunchImage;
    private boolean isShowLaunchImage;

    // progress indicator
    private RelativeLayout progressBar;
    private ProgressBar activityIndicatorCircle;

    // image upload support
    private static final int INPUT_FILE_REQUEST_CODE = 1;
    public static final String TAG = "MAWV"; // MainActivity.class.getSimpleName();
    private ValueCallback<Uri[]> mUploadMessage;
    private String mCameraPhotoPath = null;
    private long size = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        CacheUtils.clearCache(this, 0);

        GetConfigSettings();

        InitializeProgressBar();

        SetLaunchSplashImage();

        CreateWebView();
    }

    // get settings from resources->config.xml
    private void GetConfigSettings()
    {
        webViewUrl = getString(R.string.web_view_url);
        isShowLaunchImage = getResources().getBoolean(R.bool.show_launch_image);
    }

    // Launch Image Setup
    private void SetLaunchSplashImage()
    {
        myLaunchImage = (ImageView) findViewById(R.id.imageView);

        if (!isShowLaunchImage) myLaunchImage.setVisibility(View.GONE);
    }

    // Web View Setup
    private void CreateWebView()
    {
        myWebView = (WebView) findViewById(R.id. web);
        myWebView.setWebViewClient(new MyWebViewClient(this));

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUserAgentString("Chrome/56.0.0.0 Mobile");
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setSupportZoom(false);

        myWebView.setWebChromeClient(new WebChromeClient()
        {
            // The following is to support image uploads

            // For Android 5.0+
            public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> filePath, WebChromeClient.FileChooserParams fileChooserParams) {
                // Double check that we don't have any existing callbacks
                if (mUploadMessage != null) {
                    mUploadMessage.onReceiveValue(null);
                }
                mUploadMessage = filePath;
                Log.e(TAG, "myWebView.setWebChromeClient().FileCooserParams => " + filePath.toString());

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                        takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        Log.e(TAG, "myWebView.setWebChromeClient().IOException: Unable to create Image File", ex);
                    }

                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    } else {
                        takePictureIntent = null;
                    }
                }

                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                contentSelectionIntent.setType("image/*");

                Intent[] intentArray;
                if (takePictureIntent != null) {
                    intentArray = new Intent[]{takePictureIntent};
                } else {
                    intentArray = new Intent[2];
                }

                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                startActivityForResult(Intent.createChooser(chooserIntent, "Select images"), 1);

                return true;
            }
        });

        // This call requires SDK 17+
        if (Build.VERSION.SDK_INT >= 17) webSettings.setMediaPlaybackRequiresUserGesture(true);

        // Check if we have an internet connection
        // If we do not, show alert message
        if (isNetworkAvailable()) myWebView.loadUrl(webViewUrl);
        else showNoInternetAlert(webViewUrl);
    }

    // Image Processor
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  // image file name
                ".jpg",    // file extension
                storageDir      // storage folder
        );
        return imageFile;
    }

    // Image Handling
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode != INPUT_FILE_REQUEST_CODE || mUploadMessage == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        if (Build.VERSION.SDK_INT < 16) return;

        try {
            String file_path = mCameraPhotoPath.replace("file:","");
            File file = new File(file_path);
            size = file.length();

        }catch (Exception e){
            Log.e(TAG, "Error while opening image file" + e.getLocalizedMessage());
        }

        if (data != null || mCameraPhotoPath != null) {
            Integer count = 1;
            ClipData images = null;

            try {
                images = data.getClipData();
            }catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
            }

            if (images == null && data != null && data.getDataString() != null) {
                count = data.getDataString().length();
            } else if (images != null) {
                count = images.getItemCount();
            }
            Uri[] results = new Uri[count];

            // Check that the response is a good one
            if (resultCode == Activity.RESULT_OK) {
                if (size != 0) {
                    // If there is not data, then we may have taken a photo
                    if (mCameraPhotoPath != null) {
                        results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                    }
                } else if (data.getClipData() == null) {
                    results = new Uri[]{Uri.parse(data.getDataString())};
                } else {

                    for (int i = 0; i < images.getItemCount(); i++) {
                        results[i] = images.getItemAt(i).getUri();
                    }
                }
            }

            mUploadMessage.onReceiveValue(results);
            mUploadMessage = null;
        }
    }

    // Triggered when a page has started to load
    public void onPageStart()
    {
        if (isAppInitialized) ShowProgressBar();
    }

    // Triggered when a page has completely finished loading
    public void onPageFinished()
    {
        // do not go any further if we have already initialized
        // just hide progress bar
        if (isAppInitialized)
        {
            HideProgressBar();
            return;
        }

        // app loading is complete
        isAppInitialized = true;

        // clear launch image
        myWebView.flingScroll(0,0);

        // check if we are transitioning out the launch image
        if (!isShowLaunchImage) return;

        // fade out launch image
        StartLaunchImageTransitionOut();
    }

    // Launch Image Transition Fade In
    private void StartLaunchImageTransitionOut()
    {
        // Start Launch Image Transition Fade In
        int timeDelay = 1000;
        int fadeOutDuration = 850;

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setStartOffset(timeDelay + fadeOutDuration);
        fadeOut.setDuration(fadeOutDuration);

        AnimationSet animation = new AnimationSet(true);
        animation.addAnimation(fadeOut);

        animation.setAnimationListener(new Animation.AnimationListener()
        {
            public void onAnimationEnd(Animation animation)
            {
                myLaunchImage.setVisibility(View.GONE);
            }
            public void onAnimationRepeat(Animation animation)
            {

            }
            public void onAnimationStart(Animation animation)
            {

            }
        });

        myLaunchImage.requestLayout();
        myLaunchImage.setAnimation(animation);
        myLaunchImage.startAnimation(animation);
    }

    // Device Back Button Detection
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        switch (keyCode)
        {
            // detect if Device Back Button has been pressed
            case KeyEvent.KEYCODE_BACK:
                if (myWebView.canGoBack()) myWebView.goBack();
                else showExitAlert();
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    // App Alert Functions

    // Displays when user presses Device Back Button @ root home page
    private void showExitAlert()
    {
        // get exit alert text from resources->strings.xml
        String exitAlertTitle = getString(R.string.exit_alert_title);
        String exitAlertBody = getString(R.string.exit_alert_body);

        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(exitAlertTitle);
        alertDialog.setMessage(exitAlertBody);
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        MainActivity.this.finish();
                    }
                });
        alertDialog.show();

    }

    // Displays if there is no internet conneciton detected
    public void showNoInternetAlert(String url)
    {
        final String requestUrl = url;

        // get exit alert text from resources->strings.xml
        String noInternetAlertTitle = getString(R.string.no_internet_alert_title);
        String noInternetAlertBody = getString(R.string.no_internet_alert_body);

        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();

        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setTitle(noInternetAlertTitle);
        alertDialog.setMessage(noInternetAlertBody);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        if (isNetworkAvailable()) myWebView.loadUrl(requestUrl);
                        else showNoInternetAlert(requestUrl);
                    }
                });
        alertDialog.show();
    }

    // Progress Indicator Bar
    // Displays when a page is loading
    private void InitializeProgressBar()
    {
        progressBar = (RelativeLayout) findViewById(R.id.progresslayout);
        activityIndicatorCircle = (ProgressBar) findViewById(R.id.activityIndicatorCircle);
    }

    public void ShowProgressBar()
    {
        // start the spinner, only works for LOLLIPOP(21) and higher
        if (Build.VERSION.SDK_INT >= 21)
        {
            RotateDrawable rotateDrawable = (RotateDrawable) activityIndicatorCircle.getIndeterminateDrawable();
            rotateDrawable.setToDegrees(1080);
        }

        progressBar.setVisibility(View.VISIBLE);
    }

    public void HideProgressBar()
    {
        progressBar.setVisibility(View.GONE);

        // stop the spinner, only works for LOLLIPOP(21) and higher
        if (Build.VERSION.SDK_INT >= 21)
        {
            RotateDrawable rotateDrawable = (RotateDrawable) activityIndicatorCircle.getIndeterminateDrawable();
            rotateDrawable.setToDegrees(0);
        }
    }

    // Sometimes the emulator will not function exactly like a real device
    // So we need to check on that, just in case
    public static boolean isEmulator()
    {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }

    // Internet connection detection
    public boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        boolean result = activeNetworkInfo != null && activeNetworkInfo.isConnected();

        if (isEmulator()) return true;

        return result;
    }
}