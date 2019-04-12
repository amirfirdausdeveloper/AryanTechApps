package com.aryantech.atapps.Activity;

import android.app.Application;
import android.content.Context;

import com.google.firebase.FirebaseApp;

import net.doo.snap.ScanbotSDKInitializer;

/**
 * {@link ScanbotSDKInitializer} should be called
 * in {@code Application.onCreate()} method for RoboGuice modules initialization
 */
public class ExampleApplication extends Application {
    public static ExampleApplication instance;
    @Override
    public void onCreate() {
        FirebaseApp.initializeApp(this);
        new ScanbotSDKInitializer()
                // TODO add your license
                // .license(this, "YOUR_SCANBOT_SDK_LICENSE_KEY")
                .initialize(this);
        super.onCreate();
        instance = this;
    }

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }
    public static ExampleApplication getInstance() {
        return instance;
    }
}
