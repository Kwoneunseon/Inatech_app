package com.example.ui;

import android.app.Application;

public class MyApplication extends Application {
    public static String IP = "서버 외부 IP";

    @Override
    public void onCreate(){
        super.onCreate();
    }
}
