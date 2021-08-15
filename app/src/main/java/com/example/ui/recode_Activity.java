package com.example.ui;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class recode_Activity extends AppCompatActivity {
    String name_cpy = ((SubActivity) SubActivity.context_sub).name_cpy;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recode);

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(name_cpy+ " 구동이력");

    }
}
