package com.example.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

//implements View.OnClickListener
public class MainActivity extends AppCompatActivity implements ListViewBtnAdapter.ListBtnClickListener  {
    public static Context context_main;
    public String tech_name ;
    private ImageView plus_btn;
    //private Button[] buttons = new Button[3];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context_main =this;

        ListView listview;
        ListViewBtnAdapter adapter;
        ArrayList<ListViewBtnItem> items = new ArrayList<ListViewBtnItem>() ;

        loadItemsFromDB(items);

        adapter = new ListViewBtnAdapter(this,R.layout.listview_item,items,this);


        listview = (ListView) findViewById(R.id.listview1);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });


//        buttons[0] = (Button) findViewById(R.id.btn0);
//        buttons[1] = (Button) findViewById(R.id.btn1);
//        buttons[2] = (Button) findViewById(R.id.btn2);
        plus_btn = (ImageView) findViewById(R.id.plus_btn);


//        for(int i =0 ; i<3;i++){
//            buttons[i].setOnClickListener(this);
//        }

        //데이터 베이스 더하는 화면으로 넘어감.
        plus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent db_intent = new Intent(getApplicationContext(),db_Activity.class);
                startActivity(db_intent);
            }

        });

    }

    public boolean loadItemsFromDB(ArrayList<ListViewBtnItem> list){
        ListViewBtnItem item;
        int i;

        if(list == null){
            list = new ArrayList<ListViewBtnItem>();
        }
        i=1;
        item = new ListViewBtnItem();
        item.setName("UVC20_1호기");
        list.add(item);
        i++;

        item = new ListViewBtnItem();
        item.setName("UVC20_2호기");
        list.add(item);

        return true;
    }

    @Override
    public void onListBtnClick(int position) {
        Toast.makeText(this, Integer.toString(position+1) + " Item is selected..", Toast.LENGTH_SHORT).show() ;
    }

//    @Override
//    public void onClick(View v){
//        Button newButton = (Button) v;
//
//        for(Button tempButton : buttons){
//            if(tempButton == newButton){
//                tech_name = newButton.getText().toString();
//                Intent intent = new Intent(getApplicationContext(),SubActivity.class);
//                startActivity(intent);
//            }
//        }
//    }

}