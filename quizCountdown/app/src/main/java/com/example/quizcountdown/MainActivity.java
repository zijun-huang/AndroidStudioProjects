package com.example.quizcountdown;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Locale;
import java.util.Random;

/*
pending:
(1) allow user to input hours + minutes + seconds
(2) keep status at orientation changes
(3)
 */

public class MainActivity extends AppCompatActivity {
    private class Quiz{
        public boolean isPassed = false;
        public String answer;
        private int minNum = 1;
        private int maxNum = 10;

        public void generateQuiz() {
            Random r = new Random();
            int a = r.nextInt(maxNum - minNum + 1) + minNum;
            int b = r.nextInt(maxNum - minNum + 1) + minNum;
            displayQuiz(a, b);

            answer = Integer.toString(a + b);

        }

        private void displayQuiz(int a, int b) {
            String quizString = a + "+" + b + "= ?";
            mTextViewQuiz.setText(quizString);
        }
    }
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

    private TextView mTextViewQuiz;
    private TextView mEditTextAnswerToQuiz;
    private Button mButtonSubmitAnswer;


    private Quiz mQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditTextSecond = findViewById(R.id.edit_text_second);

        mTextViewCountDown = findViewById(R.id.textview_countdown);

        mButtonStartPause = findViewById(R.id.button_start_pause);
        mButtonReset = findViewById(R.id.button_reset);
        mButtonStopAlarm = findViewById(R.id.button_stop_alarm);
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm);

        mTextViewQuiz = findViewById(R.id.textview_quiz);
        mEditTextAnswerToQuiz = findViewById(R.id.edit_text_answerToQuiz);
        mButtonSubmitAnswer = findViewById(R.id.button_submit_answer);
        mQuiz = new Quiz();

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

        mButtonSubmitAnswer.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                checkAnswer();
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
        try { // take user input and start a countdown timer
            int minutes = 0;
            int seconds = Integer.parseInt(mEditTextSecond.getText().toString());

            mTimeLeftInMillis = (long) minutes * 60000 + seconds * 1000;
            timeLastSet = mTimeLeftInMillis;

            mCountDownTimer = new CountDownTimer(mTimeLeftInMillis,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mTimeLeftInMillis = millisUntilFinished;
                    updateCountDownText();
                }

                @Override
                public void onFinish() {
                    mTimerRunning = false;
                    startAlarmLoop();
                    //mediaPlayer.start();
                    updateButtonDisplay("finish timer");
                    quizUser();
                }
            }.start();

            mTimerRunning = true;
            updateButtonDisplay("start timer");

        } catch (NumberFormatException e) {
            Toast.makeText(this,"Enter value in integer only", Toast.LENGTH_LONG).show();
        }
    }

    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
        updateButtonDisplay("pause timer");
    }

    private void resetTimer() {
        mTimeLeftInMillis = timeLastSet;
        updateButtonDisplay("reset timer");
        updateCountDownText();
    }

    private void startAlarmLoop() {
        mediaPlayer.start();
        mediaPlayer.setLooping(true);
    }

    private void quizUser() {
        mQuiz.generateQuiz();
    }

    private void checkAnswer() {
        try {
            String userAnswer = mEditTextAnswerToQuiz.getText().toString();
            if (mQuiz.answer.equals(userAnswer)) {
                mQuiz.isPassed = true;
                Log.e("in CheckAnswers:", "quiz passed");

                updateButtonDisplay("pass quiz");

            } else {
                Toast.makeText(this, "Incorrect Answer", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Enter Integer Answer", Toast.LENGTH_LONG).show();
        }
    }


    private void stopAlarm() {
        mediaPlayer.stop();

        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        updateButtonDisplay("stop alarm");
        mTimeLeftInMillis = timeLastSet;
        updateCountDownText();

    }

    private void updateCountDownText() {
        int minutes = (int) mTimeLeftInMillis / 60000;
        int seconds = (int) mTimeLeftInMillis / 1000 % 60;

        String timeLeftFormated = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        mTextViewCountDown.setText(timeLeftFormated);
    }

    private void updateButtonDisplay (String status) {
        // status: ["start timer", "pause timer", "reset timer", "finish timer", "pass quiz", "stop alarm"]
        if (status.equals("start timer")) {
            mButtonStartPause.setText("Pause");
            mButtonStartPause.setVisibility(View.VISIBLE);
            mButtonReset.setVisibility(View.INVISIBLE);
        }

        if (status.equals("pause timer")) {
            mButtonStartPause.setText("Start");
            mButtonReset.setVisibility(View.VISIBLE);
        }

        if (status.equals("reset timer")) {
            mButtonReset.setVisibility(View.INVISIBLE);
            mButtonStartPause.setVisibility(View.VISIBLE);
        }

        if (status.equals("finish timer")) {
            mButtonStartPause.setText("Start");
            mButtonStartPause.setVisibility(View.INVISIBLE);
            mButtonReset.setVisibility(View.INVISIBLE);
            mTextViewQuiz.setVisibility(View.VISIBLE);
            mEditTextAnswerToQuiz.setVisibility(View.VISIBLE);
            mButtonSubmitAnswer.setVisibility(View.VISIBLE);
        }

        if (status.equals("pass quiz")) {
            mTextViewQuiz.setVisibility(View.INVISIBLE);
            mEditTextAnswerToQuiz.setVisibility(View.INVISIBLE);
            mEditTextAnswerToQuiz.setText("");
            mButtonSubmitAnswer.setVisibility(View.INVISIBLE);

            mButtonStopAlarm.setVisibility(View.VISIBLE);
        }

        if (status.equals("stop alarm")) {
            mButtonStopAlarm.setVisibility(View.INVISIBLE);
            mButtonStartPause.setVisibility(View.VISIBLE);
        }
    }
}
