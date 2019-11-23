package com.university.stopwatch.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.Chronometer;

import com.university.stopwatch.R;

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

    private Chronometer chronometer;
    private StopwatchState stopwatchState;
    private long startTime;
    private long stoppedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);

        Button startButton = findViewById(R.id.startButton);
        Button stopButton = findViewById(R.id.stopButton);
        Button resetButton = findViewById(R.id.resetButton);

        startButton.setOnClickListener(view -> {
            switch (stopwatchState) {
                case RESET: {
                    startTime = SystemClock.elapsedRealtime();
                    chronometer.setBase(startTime);
                    chronometer.start();
                    stopwatchState = StopwatchState.RUNNING;
                    stoppedTime = 0;
                    break;
                }
                case STOPPED: {
                    startTime = SystemClock.elapsedRealtime() - stoppedTime + startTime;
                    chronometer.setBase(startTime);
                    stoppedTime = 0;
                    chronometer.start();
                    stopwatchState = StopwatchState.RUNNING;
                    break;
                }
            }
        });

        stopButton.setOnClickListener(view -> {
            if (stopwatchState == StopwatchState.RUNNING) {
                chronometer.stop();
                stopwatchState = StopwatchState.STOPPED;
                stoppedTime = SystemClock.elapsedRealtime();
            }
        });

        resetButton.setOnClickListener(view -> {
            switch (stopwatchState) {
                case RUNNING: {
                    chronometer.stop();
                }
                case STOPPED: {
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    stopwatchState = StopwatchState.RESET;
                    startTime = 0;
                    stoppedTime = 0;
                    break;
                }
            }
        });

        stopwatchState = StopwatchState.RESET;
        chronometer = findViewById(R.id.chronometer);
        chronometer.setBase(SystemClock.elapsedRealtime());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putLong("time", chronometer.getBase());
        outState.putLong("startTime", startTime);
        outState.putLong("stoppedTime", stoppedTime);
        outState.putInt("isRun", stopwatchState.state);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        startTime = savedInstanceState.getLong("startTime");
        stoppedTime = savedInstanceState.getLong("stoppedTime");
        switch(savedInstanceState.getInt("isRun")) {
            case 1: {
                stopwatchState = StopwatchState.RUNNING;
                break;
            }
            case 0: {
                stopwatchState = StopwatchState.STOPPED;
                break;
            }
            case -1: {
                stopwatchState = StopwatchState.RESET;
                break;
            }
            default: {
                onDestroy();
            }
        }
        switch (stopwatchState) {
            case RUNNING: {
                chronometer.setBase(savedInstanceState.getLong("time"));
                chronometer.start();
                break;
            }
            case STOPPED: {
                chronometer.setBase(SystemClock.elapsedRealtime() - stoppedTime + startTime);
            }
        }
        super.onRestoreInstanceState(savedInstanceState);
    }
}
