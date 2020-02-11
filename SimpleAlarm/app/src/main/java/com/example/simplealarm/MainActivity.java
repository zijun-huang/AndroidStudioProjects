package com.example.simplealarm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView plainText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        plainText = findViewById(R.id.editText);
        int time = Integer.parseInt(plainText.getText().toString());
        Log.i("time", String.valueOf(time));
    }
}
