package com.example.gt_wallet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreenActivity extends AppCompatActivity {


    TextView numcount;
    ProgressBar pb;
    private int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        numcount = findViewById(R.id.numcount);

        prog();
    }

    public void prog() {
        pb = findViewById(R.id.progressBar);

        Timer t = new Timer();
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                count+=2;
                pb.setProgress(count);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        numcount.setText(String.format("%d%%", count));
                    }
                });

                if (count == 100) {
                    Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        t.schedule(tt, 0, 100);
    }

}