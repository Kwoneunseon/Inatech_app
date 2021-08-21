package com.example.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class SubActivity extends AppCompatActivity {
    //구동이력 페이지에 넘겨주기 위해서
    public static Context context_sub;
    private Intent intent;
    private String name_cpy;
    private int current_id;
    private final String TAG = "Test";

    public static final int TIMER_CODE = 1000;
    boolean power_check = true;
    private final Timer timer = new Timer();
    private TimerTask mTimerTask;
    int hour=0, min=1  ,sec=50;

    long current_time;
    Date date;
    SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd hh:mm:ss");

    Button timerBtn, recodeBtn, nameBtn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subactivity_main);

        context_sub =this;

        ImageView iv = (ImageView) findViewById(R.id.imageView1);
        iv.setImageResource(R.drawable.gray_power);
        nameBtn = (Button) findViewById(R.id.nameBtn);
        timerBtn = (Button) findViewById(R.id.timerBtn);
        recodeBtn = (Button) findViewById(R.id.recodeBtn);

        intent = getIntent();
        name_cpy = intent.getStringExtra("name");
        current_id = intent.getIntExtra("id",-1);
        nameBtn.setText(name_cpy);
        if(hour!=0) {
            timerBtn.setText(hour + "Hour " + min + "MIN " + sec + "SEC");
        }
        else{
            timerBtn.setText(min+"MIN "+sec+"SEC");
        }

////맨위의 버튼 눌렀을때( 연결할때 사용)
//        nameBtn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                Intent db_intent = new Intent(getApplicationContext(),test1_Activity.class);
//                startActivity(db_intent);
//            }
//        });


        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (power_check) {//시작
                    iv.setImageResource(R.drawable.red_power);
                    mTimerTask = createTimerTask();
                    timer.schedule(mTimerTask, 0, 1000); //Timer 실행
                    power_check = false;

                    //시간 기록
                    current_time = System.currentTimeMillis();
                    date = new Date(current_time);
                    String getTime = sdf.format(date);
                    Log.d(TAG,"지금 시각 : "+getTime);

                } else {//멈춤
                    iv.setImageResource(R.drawable.gray_power);
                    if(mTimerTask!=null)
                        mTimerTask.cancel();
                    power_check = true;

                    current_time = System.currentTimeMillis();
                    date = new Date(current_time);
                    String getTime = sdf.format(date);
                    Log.d(TAG,"지금 시각 : "+getTime);

                }
            }
            private final Handler mhandler = new Handler(Looper.getMainLooper()){
                @Override
                public void handleMessage(@NonNull Message msg) {
                    //super.handleMessage(msg);
                    switch (msg.what) {
                        case 1:
                            // 반복실행할 구문
                            // 0초 이상이면
                            if (sec != 0) {
                                //1초씩 감소
                                sec--;

                                // 0분 이상이면
                            } else if (min != 0) {
                                // 1분 = 60초
                                sec = 60;
                                sec--;
                                min--;

                                // 0시간 이상이면
                            } else if (hour != 0) {
                                // 1시간 = 60분
                                sec = 60;
                                min = 60;
                                sec--;
                                min--;
                                hour--;
                            }
                            if (hour != 0) {
                                timerBtn.setText(hour + "Hour " + min + "MIN " + sec + "SEC");
                            } else {
                                timerBtn.setText(min + "MIN " + sec + "SEC");
                            }

                            // 시분초가 다 0이라면 toast를 띄우고 타이머를 종료한다..
                            if (hour == 0 && min == 0 && sec == 0) {
                                timer.cancel();//타이머 종료
                                iv.setImageResource(R.drawable.gray_power);
                            }
                            break;
                    }
                }
            };
            private TimerTask createTimerTask(){
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        mhandler.sendEmptyMessage(1);
                    }
                };
             return timerTask;
            }

        });

        timerBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) { //private void moveSubActivity
                //여기에 타이머xml로 넘어가는 인텐트 필요
                Intent timer_intent = new Intent(SubActivity.this, Timer_Activity.class);
                startActivityResult.launch(timer_intent);
           }
        });
        
        recodeBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent recode_intent = new Intent(getApplicationContext(),recode_Activity.class);
                recode_intent.putExtra("name",name_cpy);
                recode_intent.putExtra("id",current_id);
                startActivity(recode_intent);
            }
        });
    }

    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent subIntent = result.getData();
                        hour = subIntent.getIntExtra("시",0);
                        min = subIntent.getIntExtra("분",1);
                        sec = subIntent.getIntExtra("초",50);
                        Log.d(TAG,"확인"+hour+"시"+min+"분"+sec+"초");
                        if(hour!=0) {
                            timerBtn.setText(hour + "Hour " + min + "MIN " + sec + "SEC");
                        }
                        else{
                            timerBtn.setText(min+"MIN "+sec+"SEC");
                        }


                    }
                }
            }
    );
}
