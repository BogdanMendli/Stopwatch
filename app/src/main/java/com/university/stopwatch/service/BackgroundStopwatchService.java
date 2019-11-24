package com.university.stopwatch.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class BackgroundStopwatchService extends Service {

    public static final String ACTION = "com.university.stopwatch.activity.StopwatchActivity";

    private Intent intent;
    private Handler handler = new Handler();
    private long initialTimeToMs;
    private SharedPreferences.Editor editor;

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = preferences.edit();
        initialTimeToMs = SystemClock.uptimeMillis();
        intent = new Intent(ACTION);
        handler.removeCallbacks(sendCurrentTimeAfterStart);
        handler.postDelayed(sendCurrentTimeAfterStart, 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(sendCurrentTimeAfterStart);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendTimeToActivity() {
        int secondsAfterStartService = (int) ((SystemClock.uptimeMillis() - initialTimeToMs) / 1000);
        intent.putExtra("timeAfterStartService", secondsAfterStartService);
        editor.putInt("secondsAfterStartService", secondsAfterStartService).apply();
    }

    private Runnable sendCurrentTimeAfterStart = new Runnable() {
        @Override
        public void run() {
            sendTimeToActivity();
            handler.postDelayed(this, 1000);
            sendBroadcast(intent);
        }
    };
}
