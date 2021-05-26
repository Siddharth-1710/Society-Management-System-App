package com.societymanagementsystem;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

public class Maintainance extends AppCompatActivity {

    Button payment;
    String EncKey="",ukey="";
    String payURL = IPaddress.ip+"payment.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintainance);

        //sendSMSOTP();

        payment = (Button) findViewById(R.id.btnPay);
        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pay();
            }
        });
    }

    private void pay() {

        RequestParams params = new RequestParams();
        params.put("mobile",UserData.mobile);

        final ProgressDialog pDialog = ProgressDialog.show(Maintainance.this,"Processing","please wait...",true,false);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(payURL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                pDialog.dismiss();
                String res = new String(responseBody);
                if(res.equals("200")){
                    Toast toast = Toast.makeText(Maintainance.this, "Payment Done Successfully!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();

                    Intent i = new Intent(Maintainance.this,UserHome.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(i);
                    finish();
                }else{
                    Toast toast = Toast.makeText(Maintainance.this, "Maintainance Charges Already Paid! ", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                pDialog.dismiss();
            }
        });
    }
}
