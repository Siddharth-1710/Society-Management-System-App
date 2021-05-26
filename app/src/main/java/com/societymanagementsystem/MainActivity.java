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

public class MainActivity extends AppCompatActivity {

    public static TextView adminlink,forgotpassword;
    public static EditText userid,password;
    public static Button signin,signup;
    public static String uid="",pass="";
    public static String loginurl = IPaddress.ip+"login.php";

    private String sharedPrefFile = "com.societymanagementsystem.user";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userid = (EditText) findViewById(R.id.editUserid);
        password = (EditText) findViewById(R.id.editPassword);

        signin = (Button) findViewById(R.id.btnsignin);
        signup = (Button) findViewById(R.id.btnsignup);
        adminlink = (TextView) findViewById(R.id.textLinkToAdmin);
        forgotpassword = (TextView) findViewById(R.id.textForgotPassword);

        sharedPreferences = getSharedPreferences(sharedPrefFile,MODE_PRIVATE);
        editor = sharedPreferences.edit();

        getSupportActionBar().hide();

        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // ForgotPassword.user = "user";
               Intent i = new Intent(MainActivity.this,ForgotPassword.class);
               startActivity(i);
            }
        });

        adminlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,AdminLogin.class);
                startActivity(i);
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,UserRegistration.class);
                startActivity(i);
            }
        });
    }

    public void login(){
        uid = userid.getText().toString().trim();
        pass = password.getText().toString().trim();

        if(!(uid.isEmpty()) && !(pass.isEmpty())) {

            RequestParams params = new RequestParams();
            params.put("uid", uid);
            params.put("password", pass);

            pDialog = new ProgressDialog(this);
            pDialog.setMessage("Processing...");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();

            AsyncHttpClient client = new AsyncHttpClient();
            client.get(loginurl, params, new AsyncHttpResponseHandler() {
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
                            Toast toast = Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();

                            UserData.id = obj.getString("id");
                            UserData.email = obj.getString("email");
                            UserData.mobile = obj.getString("mobile");
                            UserData.emergencyno = obj.getString("emergencyno");
                            UserData.username = obj.getString("name");
                            UserData.address = obj.getString("address");
                            UserData.city = obj.getString("city");
                            UserData.pincode = obj.getString("pincode");
                            UserData.balance = Integer.parseInt(obj.getString("balance"));
                            UserData.society = obj.getString("society");

                            editor.putString("id",UserData.id);
                            editor.putString("email",UserData.email);
                            editor.putString("mobile",UserData.mobile);
                            editor.putString("emergencyno",UserData.emergencyno);
                            editor.putString("username",UserData.username);
                            editor.putString("address",UserData.address);
                            editor.putString("city",UserData.city);
                            editor.putString("pincode",UserData.pincode);
                            editor.putString("balance",""+UserData.balance);
                            editor.putString("society",""+UserData.society);
                            editor.commit();

                            Intent i = new Intent(MainActivity.this, UserHome.class);
                            startActivity(i);
                            finish();
                        } else {
                            Toast toast = Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();

                        }
                    } catch (JSONException e) {
                        Toast toast = Toast.makeText(MainActivity.this, "Error in JSON", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    pDialog.dismiss();
                    Toast toast = Toast.makeText(MainActivity.this, "Connectivity failed with server! Try again.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }
            });
        }else{
            Toast toast = Toast.makeText(this, "Enter credentials!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }

    }
}
