package com.societymanagementsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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

import org.json.JSONObject;

import javax.mail.MessagingException;

import cz.msebera.android.httpclient.Header;

public class ForgotPassword extends AppCompatActivity {

    EditText email,mobile;
    Button next;
    String mail="",mob="",password="";
    protected static String user="";
    String credentialscheckURL = IPaddress.ip+"checkvalid.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        email = (EditText) findViewById(R.id.editForgotPasswordEmail);
        //mobile = (EditText) findViewById(R.id.editForgotPasswordMobileNo);
        next = (Button) findViewById(R.id.btnForgotPasswordNext);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mail = email.getText().toString().trim();
                //mob = mobile.getText().toString().trim();
                if(!mail.isEmpty()) {
                    checkUser();
                }
            }
        });
    }

    public void checkUser(){
        RequestParams params = new RequestParams();
        //params.put("user",user);
        params.put("email",mail);
       // params.put("mobile",mob);

        final ProgressDialog pDialog = ProgressDialog.show(ForgotPassword.this, null, "verifying information...", true, false);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(credentialscheckURL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                pDialog.dismiss();
                String res = new String(responseBody);
                try{
                    JSONObject o = new JSONObject(res);
                    if(o.getString("success").equals("200")){

                        password = o.getString("key");
                        mob = o.getString("mobile");

                        if(!mail.isEmpty()){
                            Toast.makeText(ForgotPassword.this, "Your Password Is Sent To Your Registerd E-Mail Address and Mobile Number", Toast.LENGTH_SHORT).show();
                            sendMail();

                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(mob, null, "Your Account Password Is : "+password, null, null);

                            Intent i = new Intent(ForgotPassword.this, MainActivity.class);
                            startActivity(i);
                            finish();

                        }
                    }
                }catch(Exception e){
                    Toast toast = Toast.makeText(ForgotPassword.this, res, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                pDialog.dismiss();
                Toast toast = Toast.makeText(ForgotPassword.this, "Connectivity failed! Try again.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
        });
    }

    public void sendMail(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String message = "Your Account Password Is : "+password;
                String[] files= {};
                SendMailSSL sm = new SendMailSSL();
                try {
                    sm.sendEmailWithAttachments("smtp.gmail.com", "587", "mynewjava@gmail.com", "myjavarocking", mail, "Password Recovery For Verification!",message,files);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}