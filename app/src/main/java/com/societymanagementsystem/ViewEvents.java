package com.societymanagementsystem;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViewEvents extends AppCompatActivity {

    String nam,id;
    ListView listView;

    private String eventList = IPaddress.ip+"viewevent.php";
    private ProgressDialog pDialog;
    ArrayList arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_events);

        listView = (ListView) findViewById(R.id.listView);

        eventList();
    }

    private void eventList() {

        RequestParams params = new RequestParams();
        params.put("id",UserData.society);


        pDialog = new ProgressDialog(ViewEvents.this);
        pDialog.setMessage("Verifing Details..");
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.show();

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(eventList, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                pDialog.dismiss();
                String response = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(response.toString());
                    System.out.println(object);
                    JSONArray array = object.getJSONArray("result");
                    arrayList = new ArrayList();

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object1 = array.getJSONObject(i);
                        String ename = object1.getString("event_name");
                        String date = object1.getString("date");
                        String time = object1.getString("time");
                        arrayList.add("Event Name : " + ename + "\nDate : " + date + "\nTime : " + time);

                    }
                    ArrayAdapter arrayAdapter = new ArrayAdapter(ViewEvents.this, android.R.layout.simple_list_item_1, arrayList);

                    listView.setAdapter(arrayAdapter);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ViewEvents.this, " Failed At Exception" + e, Toast.LENGTH_LONG).show();
                }
            }


            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                pDialog.dismiss();
                Toast.makeText(ViewEvents.this, " Connection Error!", Toast.LENGTH_LONG).show();
            }

        });
    }


}