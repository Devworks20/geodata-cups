package com.geodata.cups.Backend.Tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;

@SuppressLint("Registered")
public class MyApplication extends Application
{
    public static MyApplication instance;

    @Override
    public void onCreate()
    {
        super.onCreate();

        instance = this;

     // setupActivityListener();
    }

    @Override
    public Context getApplicationContext()
    {
        return super.getApplicationContext();
    }

    public static MyApplication getInstance()
    {
        return instance;
    }

    private void setupActivityListener()
    {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks()
        {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState)
            {
                activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
            }
            @Override
            public void onActivityStarted(Activity activity)
            {

            }
            @Override
            public void onActivityResumed(Activity activity)
            {

            }
            @Override
            public void onActivityPaused(Activity activity)
            {

            }
            @Override
            public void onActivityStopped(Activity activity)
            {

            }
            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState)
            {
            }
            @Override
            public void onActivityDestroyed(Activity activity)
            {

            }
        });
    }

}