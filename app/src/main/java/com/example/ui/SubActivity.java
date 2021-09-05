package com.example.ui;

import android.app.Activity;
import android.app.AsyncNotedAppOp;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
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
    private Date date;
    SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd hh:mm:ss");

    Button timerBtn, recodeBtn, nameBtn;
    private InsertData task;

    // 아두이노 연결
    private static final String TAG_T = "TCPClient";
    private boolean isConnected = false;

    private String mServerIP = null;
    private Socket mSocket = null;
    private PrintWriter mOut;
    private BufferedReader mIn;
    private Thread mReceiverThread = null;

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
                if (power_check) {//시작 (ON)
                    iv.setImageResource(R.drawable.red_power);
                    mTimerTask = createTimerTask();
                    timer.schedule(mTimerTask, 0, 1000); //Timer 실행
                    power_check = false;

                    //시간 기록
                    current_time = System.currentTimeMillis();
                    date = new Date(current_time);
                    String getTime = sdf.format(date);
                    Log.d(TAG,"지금 시각 : "+getTime);

                    task = new InsertData();
                    task.execute("http://"+MyApplication.IP+"/insert_data.php",Integer.toString(current_id),getTime,"start");


                    //와이파이 모듈에 ON데이터 전송
                    new Thread(new SenderThread("ON")).start();

                } else {//멈춤(OFF)
                    iv.setImageResource(R.drawable.gray_power);
                    if(mTimerTask!=null)
                        mTimerTask.cancel();
                    power_check = true;

                    current_time = System.currentTimeMillis();
                    date = new Date(current_time);
                    String getTime = sdf.format(date);
                    Log.d(TAG,"지금 시각 : "+getTime);
                    task = new InsertData();
                    task.execute("http://"+MyApplication.IP+"/insert_data.php",Integer.toString(current_id),getTime,"stop");

                    new Thread(new SenderThread("OFF")).start();
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

                                current_time = System.currentTimeMillis();
                                date = new Date(current_time);
                                String getTime = sdf.format(date);
                                Log.d(TAG,"지금 시각 : "+getTime);
                                task = new InsertData();
                                task.execute("http://"+MyApplication.IP+"/insert_data.php",Integer.toString(current_id),getTime,"end");

                                new Thread(new SenderThread("OFF")).start();
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
        //와이파이 모듈과 연결을 위해
        new Thread(new ConnectThread("49.174.58.125",8080)).start();
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

    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(SubActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String id = (String)params[1];
            String time = (String)params[2];
            String state  = (String)params[3];

            String serverURL = (String)params[0];
            String postParameters = "id=" + id + "&time=" + time + "&state=" +state;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();

        isConnected =false;
    }

    private class ConnectThread implements Runnable {

        private String serverIP;
        private int serverPort;

        ConnectThread(String ip, int port) {
            serverIP = ip;
            serverPort = port;

            //mConnectionStatus.setText("connecting to " + serverIP + ".......");
        }
        @Override
        public void run() {

            try {

                mSocket = new Socket(serverIP, serverPort);
                //ReceiverThread: java.net.SocketTimeoutException: Read timed out 때문에 주석처리
                //mSocket.setSoTimeout(3000);

                mServerIP = mSocket.getRemoteSocketAddress().toString();

            } catch( UnknownHostException e )
            {
                Log.d(TAG,  "ConnectThread: can't find host");
            }
            catch( SocketTimeoutException e )
            {
                Log.d(TAG, "ConnectThread: timeout");
            }
            catch (Exception e) {

                Log.e(TAG, ("ConnectThread:" + e.getMessage()));
            }


            if (mSocket != null) {

                try {

                    mOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream(), "UTF-8")), true);
                    mIn = new BufferedReader(new InputStreamReader(mSocket.getInputStream(), "UTF-8"));

                    isConnected = true;
                } catch (IOException e) {

                    Log.e(TAG, ("ConnectThread:" + e.getMessage()));
                }
            }


            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    //연결되었을 때
                    if (isConnected) {

                        Log.d(TAG, "connected to " + serverIP);
                        //mConnectionStatus.setText("connected to " + serverIP);

                        mReceiverThread = new Thread(new ReceiverThread());
                        mReceiverThread.start();
                    }else{
                        //연결안됨.

                        Log.d(TAG, "failed to connect to server " + serverIP);
                        //mConnectionStatus.setText("failed to connect to server "  + serverIP);
                    }

                }
            });
        }
    }
    private class SenderThread implements Runnable {

        private String msg;

        SenderThread(String msg) {
            this.msg = msg;
        }

        @Override
        public void run() {

            mOut.println(this.msg);
            mOut.flush();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "send message: " + msg);
                    //mConversationArrayAdapter.insert("Me - " + msg, 0);
                }
            });
        }
    }


    private class ReceiverThread implements Runnable {

        @Override
        public void run() {

            try {

                while (isConnected) {

                    if ( mIn ==  null ) {

                        Log.d(TAG, "ReceiverThread: mIn is null");
                        break;
                    }

                    final String recvMessage =  mIn.readLine();

                    if (recvMessage != null) {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                Log.d(TAG, "recv message: "+recvMessage);
                                //mConversationArrayAdapter.insert(mServerIP + " - " + recvMessage, 0);
                            }
                        });
                    }
                }

                Log.d(TAG, "ReceiverThread: thread has exited");
                if (mOut != null) {
                    mOut.flush();
                    mOut.close();
                }

                mIn = null;
                mOut = null;

                if (mSocket != null) {
                    try {
                        mSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            catch (IOException e) {

                Log.e(TAG, "ReceiverThread: "+ e);
            }
        }

    }


}
