package com.societymanagementsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class AdminLogin extends AppCompatActivity {

    Button login;
    EditText id, pass;
    TextView forgotpassword;
    String adminLoginURL = IPaddress.ip+"adminlogin.php";
    String aid="",password="";

    String sharedPrefFile = "com.societymanagementsystem.user";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        sharedPreferences = getSharedPreferences(sharedPrefFile,MODE_PRIVATE);
        editor = sharedPreferences.edit();

        login = (Button) findViewById(R.id.btnAdminLogin);
        id = (EditText) findViewById(R.id.editAdminLoginId);
        pass = (EditText) findViewById(R.id.editAdminPassword);
        forgotpassword = (TextView) findViewById(R.id.textAdminForgotPassword);

        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*ForgotPassword.user = "admin";
                Intent i = new Intent(AdminLogin.this,ForgotPassword.class);
                startActivity(i);*/
            }
        });

        getSupportActionBar().hide();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginCall();
            }
        });
    }

    public void loginCall() {
        aid = id.getText().toString().trim();
        password = pass.getText().toString().trim();

        if(aid.isEmpty() || password.isEmpty()){
            Toast toast = Toast.makeText(this,"Enter loginID and password!",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }else{

            RequestParams params = new RequestParams();
            params.put("uid", aid);
            params.put("password", password);

            final ProgressDialog pDialog = ProgressDialog.show(AdminLogin.this,"Processing","please wait...",true,false);

            AsyncHttpClient client = new AsyncHttpClient();
            client.post(adminLoginURL, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    pDialog.dismiss();
                    String response = new String(responseBody);
                    System.out.print(response);

                    try {
                        JSONObject obj = new JSONObject(response);
                        String message = obj.getString("message");
                        String success = obj.getString("success");

                        if (success.equals("200")) {
                            //String id = obj.getString("id");
                            //String medical = obj.getString("medical");

                            /*editor.putString("id",id);
                            editor.putString("medical",medical);
                            editor.commit();*/

                            Toast toast = Toast.makeText(AdminLogin.this, message, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                            Intent i = new Intent(AdminLogin.this, AdminHome.class);
                            startActivity(i);
                            finish();
                        } else {
                            Toast toast = Toast.makeText(AdminLogin.this, message, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();

                        }
                    } catch (JSONException e) {
                        Toast toast = Toast.makeText(AdminLogin.this, "Error in JSON", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                        e.getMessage();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    pDialog.dismiss();
                    Toast toast = Toast.makeText(AdminLogin.this, "Connectivity failed with server! Try again.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }
            });
        }
    }
}
