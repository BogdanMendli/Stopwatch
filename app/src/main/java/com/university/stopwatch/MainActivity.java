package com.university.stopwatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView timeView;
    private boolean isRun;
    private int secondsAfterStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timeView = findViewById(R.id.timeView);

        Button startButton = findViewById(R.id.startButton);
        Button stopButton = findViewById(R.id.stopButton);
        Button resetButton = findViewById(R.id.resetButton);

        startButton.setOnClickListener((view) -> isRun = true);

        stopButton.setOnClickListener((view) -> isRun = false);

        resetButton.setOnClickListener((view) -> {
            isRun = false;
            secondsAfterStart = 0;
            timeView.setText(R.string.initialTime);
        });

        if (savedInstanceState != null) {
            isRun = savedInstanceState.getBoolean("isRun");
            secondsAfterStart = savedInstanceState.getInt("seconds");
            timeView.setText(String.valueOf(secondsAfterStart));
        }
        runTime();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean("isRun", isRun);
        outState.putInt("seconds", secondsAfterStart);
        super.onSaveInstanceState(outState);
    }

    private void runTime() {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                if (isRun) {
                    secondsAfterStart++;
                }
                timeView.setText(setTime());
            }
        });
    }

    private String setTime() {
        return String.format(
                "%d:%02d:%02d",
                secondsAfterStart / 3600,
                secondsAfterStart / 60,
                secondsAfterStart % 60
        );
    }
}
