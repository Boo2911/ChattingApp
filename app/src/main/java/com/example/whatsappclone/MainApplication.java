package com.example.whatsappclone;

import android.app.Application;

import com.onesignal.OneSignal;


public class MainApplication extends Application {

    private static final String ONESIGNAL_APP_ID = "597ba055-fb23-4060-af60-31b1b055d0d5";

    @Override
    public void onCreate() {
        super.onCreate();
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        OneSignal.promptForPushNotifications();
    }
}
