package com.societymanagementsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class AddSociety extends AppCompatActivity {

    EditText socname, chrname, socadd, totfloor, totflat, socregno;
    Button submit;
    String sname,cname,sadd,tfloor,tflat,sregno;

    String addSocietyURL = IPaddress.ip+"addsociety.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_society);

        socname = (EditText) findViewById(R.id.editSocietyName);
        chrname = (EditText) findViewById(R.id.editChairmanName);
        socadd = (EditText) findViewById(R.id.editSocietyAddress);
        totfloor = (EditText) findViewById(R.id.editSocietyFloors);
        totflat = (EditText) findViewById(R.id.editSocietyFlats);
        socregno = (EditText) findViewById(R.id.editSocietyRegNo);

        submit = (Button) findViewById(R.id.bAddSociety);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sname = socname.getText().toString().trim();
                cname = chrname.getText().toString().trim();
                sadd = socadd.getText().toString().trim();
                tfloor = totfloor.getText().toString().trim();
                tflat = totflat.getText().toString().trim();
                sregno = socregno.getText().toString().trim();

                if(sname==""||cname==""||sadd==""||tfloor==""||tflat==""||sregno==""){
                    Toast toast = Toast.makeText(AddSociety.this,"Please Enter All Information!",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();

                }else{
                    add();
                }
            }
        });

    }

    private void add(){

        RequestParams params = new RequestParams();

        params.put("sname",sname);
        params.put("cname",cname);
        params.put("sadd",sadd);
        params.put("tfloor",tfloor);
        params.put("tflat",tflat);
        params.put("sregno",sregno);

        final ProgressDialog pDialog = ProgressDialog.show(AddSociety.this,"Processing","please wait...",true,false);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(addSocietyURL, params, new AsyncHttpResponseHandler() {

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
                        Toast.makeText(AddSociety.this,message,Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(AddSociety.this, AdminHome.class);
                        startActivity(i);

                    }else{
                        pDialog.dismiss();
                        Toast.makeText(AddSociety.this, message,Toast.LENGTH_SHORT).show();

                    }
                }catch(JSONException e){
                    pDialog.dismiss();
                    Toast.makeText(AddSociety.this,"Error in JSON",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(AddSociety.this,"Connectivity failed with server! Try again.",Toast.LENGTH_LONG).show();
            }
        });
    }
}
