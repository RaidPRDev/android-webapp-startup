# android-webapp-startup
Android Studio start up project, that allows you to create an android webview application.

## Prerequisites

#### Android Studio

Before you can start, you need to have Android Studio 3.0.0 or greater installed on your machine.<br>
To download visit https://developer.android.com/studio/

#### Google Developer License
This might seem obvious but you will need a Google Developer License in order to make a final build and publish. With the license you can create your app and application id which you will need when you configure the app's project manifest file.<br>
For more information visit https://developers.google.com/ 

___

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
___

### AndroidManifest.xml
This is the starting point and the main configuration file for your app.

Here we need to modify a few properties to make the app your own.

Edit the following:

| Property | Description |
| --- | --- |
| package="com.application.mobile" | application id |
| android:versionName="1.0.0" | application mayor version |
| android:versionCode="1" | application build version |

See here:
```xml
<manifest 
    android:versionName="1.0.0" android:versionCode="1"
    package="com.application.mobile"
    android:installLocation="preferExternal">
```

Towards the bottom of the manifest, you should see a list of permissions

```xml
<xml>    
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
</xml>
```

Change this depending on what devices you are targeting. 
```xml
<xml>
    <uses-sdk android:minSdkVersion="16" android:targetSdkVersion="27" />
</xml>
```

> You can also remove the camera permissions if you know you will not use it.
___

### config.xml
This configuration file will allow you to edit the website url, alert text and splash images. It will also allow you to add urls that you to prevent from opening new pages.  This should help prevent default behaviour when using Google or Facebook signon features.

| Property | Description |
| --- | --- |
| web_view_url | main website to load at startup |
| show_launch_image | enable or disable splash at startup |
| launch_image | splash image id [*See Splash Image section*](#user-content-splash-image) |
| disallow_url_list | filter and detect certain links and prevent default behaviour |

```xml
<resources>
    <!-- application name -->
    <string name="app_name">MyApp</string>

    <!-- main website url -->
    <string name="web_view_url">http://www.raidpr.com/clients/demo/mawv/</string>

    <!-- social url phrase for sign on -->
    <string-array name="disallow_url_list">
        <item>https://www.apple.com</item>
        <item>instagram</item>
        <item>twitter</item>
        <item>youtube</item>
        <item>vimeo</item>
    </string-array>

    <!-- native alerts -->
    <string name="exit_alert_title">Hey sad to see you go!</string>
    <string name="exit_alert_body">Are you sure you want to exit?</string>
    <string name="no_internet_alert_title">Failed Connection</string>
    <string name="no_internet_alert_body">No internet connection. Please try again.</string>

    <!-- show launch image at startup -->
    <bool name="show_launch_image">true</bool>
    <drawable name="launch_image">@drawable/launch_image_1080</drawable>
</resources>
```
___

### colors.xml

This configuration file will allow you to edit the colors used for status bar, alerts and progress indicators.

I have it setup to just change the colorPrimary property and the rest of the color properties will update.

However, you can change each color individually.

> Try not to change the `colorTextPrimary` and `colorBackground` 

```xml
<resources>
    <!-- global theme colors, primarily used for top header -->
    <color name="colorPrimary">#6195ff</color>
    <color name="colorTextPrimary">@android:color/black</color>
    <color name="colorBackground">@android:color/white</color>
    <color name="statusBarColor">@color/colorPrimary</color>
    <color name="statusBarTextColor">@color/colorTextPrimary</color>

    <!-- theme progress indicator colors -->
    <color name="progressBackground">@color/colorPrimary</color>
    <color name="progressStart">@android:color/white</color>
    <color name="progressEnd">@android:color/transparent</color>
</resources>
```
___

### Gradle Scripts/build.gradle (Module:app)
Open and you should see something similar to this:

```gradle
// for simplicity purposes, I intentionally removed the rest of the code
// we just need to focus on the defaultConfig object

android {
    compileSdkVersion 26
    defaultConfig {
        applicationId 'com.yourapplication.app'
        minSdkVersion 15
        targetSdkVersion 26
        versionCode 1
        versionName '1.00'
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
    }
   
}
```

Ideally you just need to update the `applicationId`, `versionName` and `versionCode`.

> NOTE: You may need to edit the sdk version properties.  For now leave them as is.

| Property | Description |
| --- | --- |
| compileSdkVersion | the android sdk to use to make build, leave as is |
| applicationId | add the application id created in the Google Console and the manifest |
| minSdkVersion | what minimum version do we want to support, leave as is |
| targetSdkVersion | maximum version do we want to support, leave as is |
| versionCode | build version |
| versionName | mayor version |

> IMPORTANT: Make sure you match the application id and versionName that is created in the Google Console.

___


### Splash Image
The Splash image is loaded and shown at startup. It waits until the website has loaded entirely. And then fades out. There are a variety of methods to show splash images. But for the purpose of this project we will just use (1) image for all devices. 

You can disable the splash image by setting the `show_splash_image` to **false** in the `config.xml` file.

> NOTE: While you can disable splash images, I encourage you to use it for store reviews.  In my opinion, having splash images and progress indicators make your app feel more native.

The splash image is located below.  Just replace this image with your own. The splash image resolution we need is 1920x1080, this size usually scales ok for most devices. 
```
Android
└── app                                 
    └── res
        └── drawable                    
            └── launch_image_1080.png (xxhdpi)
```

### Aplication Icons
By this point, you should have a build.  The last thing is to add icons for your application.

[AndroidAssetStudio](https://romannurik.github.io/AndroidAssetStudio/icons-launcher.html) 




