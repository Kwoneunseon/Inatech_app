package com.example.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class ListViewBtnAdapter extends RecyclerView.Adapter<ListViewBtnAdapter.CustomViewHolder>{
    String tech_name;
    private ArrayList<ListViewBtnItem> items = null;
    public static Context context_main;
    private Activity context = null;
    //private ListBtnClickListener listBtnClickListener;

    public ListViewBtnAdapter(Activity context, ArrayList<ListViewBtnItem> list){
        this.context = context;
        this.items = list;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder{
        protected Button nameBtn;
        public final View mView;
        public CustomViewHolder(@NonNull View view) {
            super(view);

            this.nameBtn = (Button) view.findViewById(R.id.list_nameBtn);
            this.mView = view;
        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listview_item,null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position){
        viewholder.nameBtn.setText(items.get(position).getName());

        viewholder.nameBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                tech_name = items.get(position).getName();
                Intent intent = new Intent(v.getContext(),SubActivity.class);
                intent.putExtra("name",tech_name);
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount(){
        return(null!=items?items.size():0);
    }
}

