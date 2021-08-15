package com.example.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import java.util.ArrayList;


public class ListViewBtnAdapter extends ArrayAdapter implements View.OnClickListener{
    String tech_name;
    public static Context context_main;
    public interface ListBtnClickListener{
        void onListBtnClick(int position);
    }
    int resourceId;
    private ListBtnClickListener listBtnClickListener;

    ListViewBtnAdapter(Context context, int resource, ArrayList<ListViewBtnItem> list, ListBtnClickListener clickListener) {
        super(context, resource, list) ;

        // resource id 값 복사. (super로 전달된 resource를 참조할 방법이 없음.)
        this.resourceId = resource ;

        this.listBtnClickListener = clickListener ;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // 생성자로부터 저장된 resourceId(listview_btn_item)에 해당하는 Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(this.resourceId/*R.layout.listview_btn_item*/, parent, false);
        }
        final Button nameBtn = (Button) convertView.findViewById(R.id.list_nameBtn);

        final ListViewBtnItem listViewItem = (ListViewBtnItem) getItem(position);

        nameBtn.setText(listViewItem.getName());

        //nameBtn을 선택했을 때
        nameBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            tech_name = listViewItem.getName();
            Intent intent = new Intent(context,SubActivity.class);
            intent.putExtra("name",tech_name);
            context.startActivity(intent);

            }
        });
        //설정/삭제 버튼을 선택했을 때
        Button setBtn = (Button) convertView.findViewById(R.id.list_setBtn);
        setBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            }
        });
        return convertView;
    }

    @Override
    public void onClick(View v) {
        if(this.listBtnClickListener !=null)
            this.listBtnClickListener.onListBtnClick((int)v.getTag());
    }
}

