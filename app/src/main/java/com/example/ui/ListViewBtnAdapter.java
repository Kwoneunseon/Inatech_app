package com.example.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class ListViewBtnAdapter extends RecyclerView.Adapter<ListViewBtnAdapter.CustomViewHolder>{
    String tech_name;
    private ArrayList<ListViewBtnItem> items = null;
    public static Context context_main;
    private Activity context = null;
    private Integer current_id;


    public ListViewBtnAdapter(Activity context, ArrayList<ListViewBtnItem> list){
        this.context = context;
        this.items = list;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder{
        protected Button nameBtn;
        //protected Button deleteBtn;
        public final View mView;
        public CustomViewHolder(@NonNull View view) {
            super(view);

            this.nameBtn = (Button) view.findViewById(R.id.list_nameBtn);
            //this.deleteBtn =(Button)view.findViewById(R.id.list_deleteBtn);
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
                current_id = items.get(position).getID();
                Intent intent = new Intent(v.getContext(),SubActivity.class);
                intent.putExtra("name",tech_name);
                intent.putExtra("id",current_id);
                context.startActivity(intent);
            }
        });

//        viewholder.deleteBtn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//
//                InsertData task = new InsertData();
//                task.execute("http://" + MyApplication.IP + "/delete.php", Integer.toString(current_id));
//
//            }
//        });





    }



    @Override
    public int getItemCount(){
        return(null!=items?items.size():0);
    }
}
//
//class InsertData extends AsyncTask<String, Void, String> {
//    ProgressDialog progressDialog;
//    public static String TAG = "phptest";
//
//    @Override
//    protected void onPreExecute() {
//        super.onPreExecute();
//
//        //progressDialog = ProgressDialog.show(db_Activity.this,
//        //        "Please Wait", null, true, true);
//    }
//
//
//    @Override
//    protected void onPostExecute(String result) {
//        super.onPostExecute(result);
//
//        progressDialog.dismiss();
//        // Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG);
//        Log.d(TAG, "POST response  - " + result);
//    }
//
//    @Override
//    protected String doInBackground(String... params) {
//
//        String id = (String)params[1];
//        String serverURL = (String)params[0];
//        String postParameters = "id=" + id ;
//
//
//        try {
//
//            URL url = new URL(serverURL);
//            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//
//
//            httpURLConnection.setReadTimeout(5000);
//            httpURLConnection.setConnectTimeout(5000);
//            httpURLConnection.setRequestMethod("POST");
//            httpURLConnection.connect();
//
//
//            OutputStream outputStream = httpURLConnection.getOutputStream();
//            outputStream.write(postParameters.getBytes("UTF-8"));
//            outputStream.flush();
//            outputStream.close();
//
//
//            int responseStatusCode = httpURLConnection.getResponseCode();
//            Log.d(TAG, "POST response code - " + responseStatusCode);
//
//            InputStream inputStream;
//            if(responseStatusCode == HttpURLConnection.HTTP_OK) {
//                inputStream = httpURLConnection.getInputStream();
//            }
//            else{
//                inputStream = httpURLConnection.getErrorStream();
//            }
//
//
//            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
//            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//
//            StringBuilder sb = new StringBuilder();
//            String line = null;
//
//            while((line = bufferedReader.readLine()) != null){
//                sb.append(line);
//            }
//
//
//            bufferedReader.close();
//
//
//            return sb.toString();
//
//
//        } catch (Exception e) {
//
//            Log.d(TAG, "InsertData: Error ", e);
//
//            return new String("Error: " + e.getMessage());
//        }
//
//    }
//}