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

public class ViewComplaints extends AppCompatActivity {

    String nam,id;
    ListView listView;

    private String complaintList = IPaddress.ip+"viewcomplaint.php";
    private ProgressDialog pDialog;
    ArrayList arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_complaints);


        listView = (ListView) findViewById(R.id.listView2);

        complaintsList();
    }

    private void complaintsList() {

        RequestParams params = new RequestParams();
        params.put("id", 1);

        pDialog = new ProgressDialog(ViewComplaints.this);
        pDialog.setMessage("Verifing Details..");
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.show();

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(complaintList, params, new AsyncHttpResponseHandler() {

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
                        String society = object1.getString("society");
                        String owner = object1.getString("owner");
                        String flatno = object1.getString("flatno");
                        String complaint = object1.getString("complaint");

                        arrayList.add("Society : " + society + "\nOwner : " + owner + "\nFlat No : " + flatno + "\nComplaint : " + complaint);

                    }
                    ArrayAdapter arrayAdapter = new ArrayAdapter(ViewComplaints.this, android.R.layout.simple_list_item_1, arrayList);

                    listView.setAdapter(arrayAdapter);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ViewComplaints.this, " Failed At Exception" + e, Toast.LENGTH_LONG).show();
                }
            }


            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                pDialog.dismiss();
                Toast.makeText(ViewComplaints.this, " Connection Error!", Toast.LENGTH_LONG).show();
            }

        });
    }

}
