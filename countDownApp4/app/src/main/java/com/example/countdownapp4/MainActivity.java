// https://www.youtube.com/playlist?list=PLrnPJCHvNZuB8wxqXCwKw2_NkyEmFwcSd

package com.example.countdownapp4;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final long START_TIME_IN_MILLIS = 0;
    private long timeLastSet;
    //private TextView mEditTextMinute;
    private TextView mEditTextSecond;
    private TextView mTextViewCountDown;
    private Button mButtonStartPause;
    private Button mButtonReset;
    private Button mButtonStopAlarm;

    private CountDownTimer mCountDownTimer;

    private boolean mTimerRunning;

    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;

    private MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mEditTextMinute = findViewById(R.id.edit_text_minute);
        mEditTextSecond = findViewById(R.id.edit_text_second);

        mTextViewCountDown = findViewById(R.id.textview_countdown);

        mButtonStartPause = findViewById(R.id.button_start_pause);
        mButtonReset = findViewById(R.id.button_reset);
        mButtonStopAlarm = findViewById(R.id.button_stop_alarm);
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm);

        mButtonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimerRunning) {
                    pauseTimer();
                } else {
                    startTimer();
                }
            }
        });

        mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });

        mButtonStopAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAlarm();
            }
        });

        updateCountDownText();
    }


    private void startTimer() {

        try {
            int minutes = 0;
            //int minutes = Integer.parseInt(mEditTextMinute.getText().toString());
            int seconds = Integer.parseInt(mEditTextSecond.getText().toString());

            mTimeLeftInMillis = (long) minutes*60000 + seconds*1000;
            //mTimeLeftInMillis = (long) minutes * 60000;
            timeLastSet = mTimeLeftInMillis;

            mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mTimeLeftInMillis = millisUntilFinished;
                    updateCountDownText();
                }

                @Override
                public void onFinish() {
                    mTimerRunning = false;

                    mButtonStartPause.setText("Start");
                    mButtonStartPause.setVisibility(View.INVISIBLE);
                    mButtonReset.setVisibility(View.INVISIBLE);
                    mButtonStopAlarm.setVisibility(View.VISIBLE);
                    mediaPlayer.start();
                }
            }.start();

            mTimerRunning = true;

            mButtonStartPause.setText("Pause");
            mButtonStartPause.setVisibility(View.VISIBLE);
            mButtonReset.setVisibility(View.INVISIBLE);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Enter Value in integer only", Toast.LENGTH_LONG).show();
        }
    }
    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
        mButtonStartPause.setText("Start");
        mButtonReset.setVisibility(View.VISIBLE);
    }

    private void resetTimer() {
        mTimeLeftInMillis = timeLastSet;
        mButtonReset.setVisibility(View.INVISIBLE);
        mButtonStartPause.setVisibility(View.VISIBLE);
        updateCountDownText();
    }

    private void stopAlarm() {
        mediaPlayer.stop();
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mButtonStopAlarm.setVisibility(View.INVISIBLE);
        mButtonStartPause.setVisibility(View.VISIBLE);
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();
    }

    private void updateCountDownText() {
        int minutes = (int) mTimeLeftInMillis / 60000;
        int seconds = (int) mTimeLeftInMillis / 1000 % 60;
        // format the values
        String timeLeftFormatted = String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds);
        mTextViewCountDown.setText(timeLeftFormatted);
    }
}
