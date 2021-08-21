package com.example.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class recode_Activity extends AppCompatActivity {
    //구동이력 위에 써주기 위해
//    String name_cpy = ((SubActivity) SubActivity.context_sub).name_cpy;

    private static String IP_ADDRESS = "192.168.35.28";
    private static String TAG = "phptest";
    private TextView record_list;
    private TextView title;
    private String mJsonString;
    private int current_id; //현재 id
    private String name_cpy;
    private Intent intent;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recode);

        intent = getIntent();
        current_id = intent.getIntExtra("id",-1);
        name_cpy = intent.getStringExtra("name");

        title = (TextView) findViewById(R.id.title);
        record_list = (TextView)findViewById(R.id.record_list);

        title.setText(name_cpy+ " 구동이력");

        GetData task = new GetData();
        task.execute("http://"+IP_ADDRESS+"/getrecord.php","");

    }

    private class GetData extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();

            progressDialog = progressDialog.show(recode_Activity.this,"please wait",null,true,true);
        }

        @Override
        protected  void onPostExecute(String result){
            super.onPostExecute(result);

            progressDialog.dismiss();
            mJsonString = result;
            showResult();

        }

        @Override
        protected  String doInBackground(String... params){
            String serverURL = params[0];
            String postParmeters = params[1];

            try{
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParmeters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG,"response code - "+responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode ==HttpURLConnection.HTTP_OK){
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream,"UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine())!=null){
                    sb.append(line);
                }
                bufferedReader.close();
                return sb.toString().trim();
            }catch (Exception e){
                Log.d(TAG,"GetData : Error",e);
                errorString = e.toString();

                return null;
            }
        }

    }
    private void showResult(){
        String TAG_JSON = "systems";
        String TAG_ID = "id";
        String TAG_TIME ="time";
        String TAG_STATE="state";

        try{
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i = 0;i<jsonArray.length();i++){
                JSONObject item = jsonArray.getJSONObject(i);

                int id = item.getInt(TAG_ID);
                String time = item.getString(TAG_TIME);
                String state = item.getString(TAG_STATE);

                //여기에 정보 리스트에 집어넣기
                if(current_id==id) {
                    String str = String.format(getResources().getString(R.string.textview_message), time, state);
                    record_list.append(str);
                }

            }

        }catch (JSONException e){
            Log.d(TAG,"showResult : ",e);

        }
    }

}


