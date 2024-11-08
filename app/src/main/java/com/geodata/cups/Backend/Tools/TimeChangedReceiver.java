package com.geodata.cups.Backend.Tools;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeChangedReceiver extends BroadcastReceiver
{
    private static final String TAG = TimeChangedReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent)
    {
        @SuppressLint("SimpleDateFormat")
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        Date changedTime = Calendar.getInstance().getTime();

        Log.i(TAG, "Date/Time has been Modified!");

        try
        {
            String Details = "Date/Time has been Modified.  " + changedTime;
            String mobileType = Build.MODEL + " -- " + Build.VERSION.RELEASE;
            Date now = new Date(System.currentTimeMillis());
            String dateTime = dateFormat.format(now);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }
}
