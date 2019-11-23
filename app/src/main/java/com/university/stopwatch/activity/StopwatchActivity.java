package com.university.stopwatch.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.university.stopwatch.R;
import com.university.stopwatch.service.BackgroundStopwatchService;

public class StopwatchActivity extends AppCompatActivity {

    enum StopwatchState {
        RUNNING(1),
        STOPPED(0),
        RESET(-1);

        private int state;

        StopwatchState(int state) {
            this.state = state;
        }
    }

    private int currentTimeSeconds = 0;
    private int timeBeforeStopped = 0;
    private TextView timeView;
    private StopwatchState stopwatchState;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);

        stopwatchState = StopwatchState.RESET;

        Button startButton = findViewById(R.id.startButton);
        Button stopButton = findViewById(R.id.stopButton);
        Button resetButton = findViewById(R.id.resetButton);

        timeView = findViewById(R.id.timeView);

        startButton.setOnClickListener(view -> {
            switch (stopwatchState) {
                case RESET:
                case STOPPED: {
                    stopwatchState = StopwatchState.RUNNING;
                    startTimer();
                }
            }
        });

        stopButton.setOnClickListener(view -> {
            if (stopwatchState == StopwatchState.RUNNING || stopwatchState == StopwatchState.RESET) {
                stopTimer();
                stopwatchState = StopwatchState.STOPPED;
                currentTimeSeconds += timeBeforeStopped;
                timeBeforeStopped = 0;
            }
        });

        resetButton.setOnClickListener(view -> {
            switch (stopwatchState) {
                case RESET:
                case RUNNING: {
                    stopTimer();
                }
                case STOPPED: {
                    timeBeforeStopped = 0;
                    currentTimeSeconds = 0;
                    timeView.setText(R.string.initialTime);
                    stopwatchState = StopwatchState.RESET;
                    break;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(BackgroundStopwatchService.ACTION));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }

    private void setTimer(Intent intent) {
        timeBeforeStopped = intent.getIntExtra("timeAfterStartService", 0);
        int timeWorkingStopwatch = timeBeforeStopped + currentTimeSeconds;
        int hours = timeWorkingStopwatch / 3600;
        int minutes = (timeWorkingStopwatch / 60) & 60;
        int seconds = timeWorkingStopwatch % 60;
        timeView.setText(String.format("%d:%02d:%02d", hours, minutes, seconds));
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setTimer(intent);
        }
    };

    private void stopTimer() {
        stopService(new Intent(getApplicationContext(), BackgroundStopwatchService.class));
    }

    private void startTimer() {
        startService(new Intent(getApplicationContext(), BackgroundStopwatchService.class));
    }
}
