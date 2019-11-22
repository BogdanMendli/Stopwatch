package com.university.stopwatch.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.Chronometer;

import com.university.stopwatch.R;

public class MainActivity extends AppCompatActivity {

    private Chronometer chronometer;
    private boolean isRun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startButton = findViewById(R.id.startButton);
        Button stopButton = findViewById(R.id.stopButton);
        Button resetButton = findViewById(R.id.resetButton);

        startButton.setOnClickListener(view -> {
            if (!isRun) {
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
                isRun = true;
            }
        });

        stopButton.setOnClickListener(view -> {
            if (isRun) {
                chronometer.stop();
                isRun = false;
            }
        });

        resetButton.setOnClickListener(view -> {
            if (isRun) {
                chronometer.stop();
                isRun = false;
            }
            chronometer.setBase(SystemClock.elapsedRealtime());
        });

        chronometer = findViewById(R.id.chronometer);
        chronometer.setBase(SystemClock.elapsedRealtime());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putLong("time", chronometer.getBase());
        outState.putBoolean("isRun", isRun);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        chronometer.setBase(savedInstanceState.getLong("time"));
        isRun = savedInstanceState.getBoolean("isRun");
        if (isRun) {
            chronometer.start();
        }
        super.onRestoreInstanceState(savedInstanceState);
    }
}
