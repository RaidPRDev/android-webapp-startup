# android-webapp-startup
Android Studio start up project, that allows you to create an android webview application.

## Prerequisites

#### Android Studio

Before you can start, you need to have Android Studio 3.0.0 or greater installed on your machine.<br>
To download visit https://developer.android.com/studio/

## Project Setup

Launch Android Studio, and select 'Open an existing Android Project project'

Find where you saved the project and choose the webview_mobile_app/android folder.

Once loaded, look at the Project Window and you should see something like this:
```
Android
├── app                                 # app source code 
└── Gradle Scripts                      # project build scripts
```

In Android Studio Project Window, open the app and Gradle Scripts folder contents. <br>

You should have something similar to this:
```
Android
├── app                                 # app source code 
    ├── manifests
        └── AndroidManifest.xml         # application base configuration
    ├── java
        └── com.application.mobile
            ├── CacheUtils              # browser cache maintenance 
            ├── MainActivity            # application root 
            ├── MyWebViewClient         # web view event handlers
            └── NetworkUtils            # network handlers
    ├── res
        ├── drawable                    #
        ├── layout
        ├── mipmap
        └── values
            ├── colors.xml
            ├── config.xml
            ├── strings.xml
            └── styles.xml              # network handlers

└── Gradle Scripts                      # project build scripts
    ├── build.gradle (Project:android)
    ├── build.gradle (Module:app)
    └── other files ...
```

Now we only need to worry about a few files:
```
    > app/manifest/AndroidManifest.xml
    > app/res/values/config.xml
    > app/res/values/colors.xml
    > Gradle Scripts/build.gradle (Module:app)
```

#### AndroidManifest.xml
This is the starting point and the main configuration file for your app.

Here we need to modify a few properties to make the app your own.

android:versionName="1.0.0" android:versionCode="1"
package="com.application.mobile"

Towards the bottom of the manifest, you should see a list of permissions
```xml
<manifest>    
    <uses-sdk android:minSdkVersion="16" android:targetSdkVersion="27" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.CAMERA2" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.hardware.camera"></uses-permission>
    <uses-feature android:name="android.hardware.camera" />
</manifest>
```




#### webpack.js
Get familiar with webpack go to https://webpack.js.org

#### firebase sdk
Get familiar with firebase go to https://firebase.google.com/
