package com.example.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Enumeration;

//implements ListViewBtnAdapter.ListBtnClickListener
public class MainActivity extends AppCompatActivity  {

    //php
    private static String TAG = "phptest";

    public static Context context_main;
    public String tech_name ;
    private ImageView plus_btn;
    private RecyclerView mRecyclerView;
    private String mJsonString;
    private ArrayList<ListViewBtnItem> items;
    private ListViewBtnAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Log.d(TAG,"Ip주소는 :"+getIpAddress());

        context_main =this;
        mRecyclerView = (RecyclerView) findViewById(R.id.listview1);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        items = new ArrayList<ListViewBtnItem>() ;

        adapter = new ListViewBtnAdapter(this,items);
        mRecyclerView.setAdapter(adapter);

        items.clear();
        adapter.notifyDataSetChanged();

        GetData task = new GetData();
        task.execute("http://"+MyApplication.IP+"/getjson.php","");


        plus_btn = (ImageView) findViewById(R.id.plus_btn);

        plus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent db_intent = new Intent(getApplicationContext(),db_Activity.class);
                startActivity(db_intent);
            }

        });

    }

    private class GetData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MainActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "response - " + result);
            mJsonString = result;
            showResult();

        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            String postParameters = params[1];


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

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
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "GetData : Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }

//   void checkAvailableConnection(){
//        ConnectivityManager connMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//
//        final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//
//        if(wifi.isAvailable()){
//            WifiManager myWifiManager = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
//            WifiInfo myWifiInfo = myWifiManager.getConnectionInfo();
//            int ipAddress = myWifiInfo.getIpAddress();
//            System.out.println("Wifi 주소는 "+android.text.format.Formatter.formatIpAddress(ipAddress));
//        }
//
//    }


//    //IP주소 가져오기
//    public static String getIpAddress() {
//        try {
//            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
//
//                NetworkInterface intf = en.nextElement();
//
//                //네트워크 중에서 IP가 할당된 넘들에 대해서 뺑뺑이를 한 번 더 돕니다.
//                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
//
//                    InetAddress inetAddress = enumIpAddr.nextElement();
//
//                    //네트워크에는 항상 Localhost 즉, 루프백(LoopBack)주소가 있으며, 우리가 원하는 것이 아닙니다.
//                    //IP는 IPv6와 IPv4가 있습니다.
//                    //IPv6의 형태 : fe80::64b9::c8dd:7003
//                    //IPv4의 형태 : 123.234.123.123
//                    //어떻게 나오는지는 찍어보세요.
//                    if (inetAddress.isLoopbackAddress()) {
//                        Log.i("IPAddress", intf.getDisplayName() + "(loopback) | " + inetAddress.getHostAddress());
//                    } else {
//                        Log.i("IPAddress", intf.getDisplayName() + " | " + inetAddress.getHostAddress());
//                    }
//
//                    //루프백이 아니고, IPv4가 맞다면 리턴~~~
//                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
//                        return inetAddress.getHostAddress().toString();
//                    }
//                }
//            }
//        }catch (SocketException ex){
//            ex.printStackTrace();
//        }
//        return null;
//    }
//
//
//


    private void showResult(){

        String TAG_JSON="systems";
        String TAG_NAME = "name";
        String TAG_ID = "id";

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            Log.d(TAG,jsonArray.length()+"임");
            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String name = item.getString(TAG_NAME);
                Integer currnet_id = item.getInt(TAG_ID);
                ListViewBtnItem listviewBtnitem = new ListViewBtnItem();

                listviewBtnitem.setName(name);
                listviewBtnitem.setID(currnet_id);

                items.add(listviewBtnitem);
                adapter.notifyDataSetChanged();
            }



        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }

}