package com.societymanagementsystem;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

public class Verification extends AppCompatActivity {

    EditText otpverification;
    Button verify;
    String EncKey="",ukey="";
    String paymentURL = IPaddress.ip+"addbalance.php";

    InitializeSharedPreferences isp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        isp = new InitializeSharedPreferences(this);

        otpverification = (EditText) findViewById(R.id.editVerificationPin);
        verify = (Button) findViewById(R.id.btnVerify);

        sendSMSOTP();

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ukey = otpverification.getText().toString().trim();
                if(EncKey.equals(ukey)){
                    callPayment();
                }
            }
        });

    }

    private void sendSMSOTP(){
        if(ActivityCompat.checkSelfPermission(Verification.this, Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Verification.this,new String[]{Manifest.permission.SEND_SMS},1);
        }else{
            int randpin = (int)(Math.random()*9000)+1000;
            EncKey = ""+randpin;
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(UserData.mobile,null,"Your OTP for transaction is "+EncKey,null,null);
        }
    }

    private void callPayment(){
        RequestParams params = new RequestParams();
        params.put("card",UserData.crd);
        params.put("paying",UserData.amountpaying);

        final ProgressDialog pDialog = ProgressDialog.show(Verification.this,"Processing","please wait...",true,false);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(paymentURL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                pDialog.dismiss();
                String res = new String(responseBody);
                if(res.equals("200")){
                    Toast toast = Toast.makeText(Verification.this, "Payment Done Successfully!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();

                    UserData.amountpaying = 0;
                    Intent i = new Intent(Verification.this,UserHome.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(i);
                    finish();
                }else{
                    Toast toast = Toast.makeText(Verification.this, "Transaction Failed! Please try again. "+res, Toast.LENGTH_SHORT);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 1: if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                int randpin = (int)(Math.random()*900000)+100000;
                EncKey = ""+randpin;
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(UserData.mobile,null,"Your OTP for transaction is "+EncKey,null,null);
            }else{
                ActivityCompat.requestPermissions(Verification.this,new String[]{Manifest.permission.SEND_SMS},1);
            }
        }
    }
}
