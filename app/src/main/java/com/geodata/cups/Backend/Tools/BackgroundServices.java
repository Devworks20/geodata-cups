package com.geodata.cups.Backend.Tools;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.util.Timer;
import java.util.TimerTask;

public class BackgroundServices extends Service
{
    private static final String TAG = BackgroundServices.class.getSimpleName();

    public int counter = 0;
    private Timer timer;
    private TimerTask timerTask;
    private boolean controlSync = true;

    @Override
    public void onCreate()
    {
        super.onCreate();

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground()
    {
        Log.i("Count", "STARTTTTTTTTTTTTTTTTTTTTTTTT");


        String NOTIFICATION_CHANNEL_ID = "com.geodata.cups";
        String channelName = "Background Service";

        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();

        startForeground(2, notification);

        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        startService(restartServiceIntent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        super.onStartCommand(intent, flags, startId);

        startTimer();

        return START_STICKY;
    }

    public void startTimer()
    {

        if (controlSync)
        {
            Log.i("Count", "START");

            controlSync = false;

            timer = new Timer();
            timerTask = new TimerTask()
            {
                public void run()
                {
                    if(haveNetworkConnection(getApplicationContext()))
                    {
                        Log.i(TAG, "Count: " + "=========  " + (counter++));
                    }
                    else
                    {
                        Log.i(TAG, "STOPPED");
                    }
                }
            };
            timer.schedule(timerTask, 1000, 10000); //
            //timer.schedule(timerTask, 60000, 1000); //
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        StopTimerTask();

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, BackgroundServicesReset.class);
        this.sendBroadcast(broadcastIntent);
    }

    public void StopTimerTask()
    {
        if (timer != null)
        {
            timer.cancel();
            timer = null;
        }

        if(timerTask != null)
        {
            timerTask.cancel();
            timerTask = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    //Validate Network
    private boolean haveNetworkConnection(Context context)
    {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork != null)
        {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
            {
                haveConnectedWifi = true;
            }
            else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
            {
                haveConnectedMobile = true;
            }
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

}
