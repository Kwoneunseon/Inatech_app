package com.example.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Timer_Activity extends AppCompatActivity {
    int hour = 0, min=1, sec= 50;
    private final String TAG = "Test";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timer_main);

        Button saveBtn = (Button)findViewById(R.id.saveBtn);
        EditText hourET = (EditText)findViewById(R.id.hourET);
        EditText minET = (EditText)findViewById(R.id.minuteET);
        EditText secET = (EditText)findViewById(R.id.secondET);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hour = Integer.parseInt((hourET.getText().toString().length()<=0)?"0":hourET.getText().toString().trim());
                min = Integer.parseInt((minET.getText().toString().length()<=0)?"0":minET.getText().toString().trim());
                sec = Integer.parseInt((secET.getText().toString().length()<=0)?"0":secET.getText().toString().trim());
                Log.d(TAG,hour+"시"+min+"분"+sec+"초");

                Intent intent = new Intent();
                intent.putExtra("시",hour);
                intent.putExtra("분",min);
                intent.putExtra("초",sec);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

    }
}
