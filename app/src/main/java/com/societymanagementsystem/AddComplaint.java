package com.societymanagementsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class AddComplaint extends AppCompatActivity {


    Spinner spinner;
    List society_name;
    String society="";
    Button addComplaint;

    EditText owner,flatno,complaint;
    String ownr,flno,cmplnt;

    String getSocietyURL = IPaddress.ip+"ca.php";
    String addComplaintURL = IPaddress.ip+"addcomplaint.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_complaint);

        getSociety();

        owner = (EditText) findViewById(R.id.editOwner);
        flatno = (EditText) findViewById(R.id.editFlatNo);
        complaint =  (EditText) findViewById(R.id.editComplaint);

        addComplaint = (Button) findViewById(R.id.bAddComplaint);

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                society = parent.getItemAtPosition(position).toString();
                //Toast.makeText(AddComplaint.this, society+" Society Is Selected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(AddComplaint.this, "Please Select A Society!", Toast.LENGTH_SHORT).show();
            }
        });

        addComplaint.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {



                ownr = owner.getText().toString().trim();
                flno = flatno.getText().toString().trim();
                cmplnt = complaint.getText().toString().trim();

                if(ownr.isEmpty() || flno.isEmpty() || cmplnt.isEmpty() || society.isEmpty()){
                    Toast.makeText(AddComplaint.this, "Enter All Required Details!", Toast.LENGTH_SHORT).show();
                }else{
                    addComplaints();
                }
            }
        });

    }

    private void getSociety() {

        RequestParams params = new RequestParams();
        params.put("id", UserData.id);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(getSocietyURL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String response = new String(responseBody);
                try {

                    JSONObject object = new JSONObject(response);
                    JSONArray array = object.getJSONArray("result");

                    society_name = new ArrayList();

                    for (int i = 0; i < array.length(); i++) {

                        JSONObject object1 = array.getJSONObject(i);

                        String det = object1.getString("society_name");

                        society_name.add(det);
                    }

                    ArrayAdapter a1 = new ArrayAdapter(AddComplaint.this, android.R.layout.simple_spinner_dropdown_item, society_name);
                    spinner.setAdapter(a1);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(AddComplaint.this, " Failed At Exception" + e, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                Toast.makeText(AddComplaint.this, "Connectivity Error" , Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addComplaints(){

        RequestParams params = new RequestParams();
        params.put("owner",ownr);
        params.put("society",society);
        params.put("flatno",flno);
        params.put("complaint",cmplnt);

        final ProgressDialog pDialog = ProgressDialog.show(AddComplaint.this,"Processing","please wait...",true,false);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(addComplaintURL, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String response = new String(responseBody);
                System.out.print(response);

                try {
                    JSONObject obj = new JSONObject(response);
                    String message = obj.getString("message");
                    String success = obj.getString("success");

                    if(success.equals("200")){
                        pDialog.dismiss();
                        Toast.makeText(AddComplaint.this,message,Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(AddComplaint.this, UserHome.class);
                        startActivity(i);

                    }else{
                        pDialog.dismiss();
                        Toast.makeText(AddComplaint.this,message,Toast.LENGTH_SHORT).show();

                    }
                }catch(JSONException e){
                    pDialog.dismiss();
                    Toast.makeText(AddComplaint.this,"Error in JSON",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(AddComplaint.this,"Connectivity failed with server! Try again.",Toast.LENGTH_LONG).show();
            }
        });
    }
}
