package com.university.stopwatch.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import com.university.stopwatch.R;
import com.university.stopwatch.service.BackgroundStopwatchService;

public class StopwatchActivity extends AppCompatActivity {

    private TextView timeView;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private int secondsBefore;
    private int seconds;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);

        timeView = findViewById(R.id.timeView);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = preferences.edit();
        secondsBefore = preferences.getInt("secondsBefore", 0);
        seconds = preferences.getInt("seconds", 0);
        if (preferences.getBoolean("isRun", false)) {
            setTimer(preferences.getInt("secondsBefore", 0) + preferences.getInt("secondsAfterStartService", 0));
        } else {
            setTimer(preferences.getInt("secondsBefore", 0));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(BackgroundStopwatchService.ACTION));
    }

    @Override
    protected void onStop() {
        super.onStop();
        editor.putInt("secondsBefore", secondsBefore).apply();
        unregisterReceiver(broadcastReceiver);
    }

    private void setTimer(int secondsAfterStart) {
        editor.putInt("seconds", secondsAfterStart).apply();
        int hours = secondsAfterStart / 3600;
        int minutes = (secondsAfterStart / 60);
        int seconds = secondsAfterStart % 60;
        timeView.setText(String.format("%d:%02d:%02d", hours, minutes, seconds));
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            seconds = secondsBefore + intent.getIntExtra("timeAfterStartService", 0);
            setTimer(seconds);
        }
    };

    private void stopTimer() {
        stopService(new Intent(getApplicationContext(), BackgroundStopwatchService.class));
    }

    private void startTimer() {
        startService(new Intent(getApplicationContext(), BackgroundStopwatchService.class));
    }

    public void onStartButtonClick(View view) {
        if (!preferences.getBoolean("isRun", false)) {
            startTimer();
        }
        editor.putBoolean("isRun", true).apply();
    }

    public void onStopButtonClick(View view) {
        if (preferences.getBoolean("isRun", false)) {
            editor.putInt("secondsBefore", secondsBefore).apply();
            secondsBefore = seconds;
            stopTimer();
        }
        editor.putBoolean("isRun", false).apply();
    }

    public void onResetButtonClick(View view) {
        seconds = 0;
        secondsBefore = 0;
        editor.putInt("secondsBefore", 0).apply();
        editor.putInt("seconds", 0).apply();
        editor.putBoolean("isRun", false).apply();
        timeView.setText(R.string.initialTime);
        stopTimer();
    }
}
