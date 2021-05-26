package com.societymanagementsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class UserHome extends AppCompatActivity {

    TextView title;
    Button maintainance, addbalance, logout, viewevent, addcomplaint;

    InitializeSharedPreferences isp;

    //String getAllDetailsURL = IPaddress.ip+"getalldetails.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        isp = new InitializeSharedPreferences(this);

        title = (TextView) findViewById(R.id.textUserHomeHeading);
        title.setText("Welcome "+UserData.username);
        maintainance = (Button) findViewById(R.id.btnMaintainance);
        addbalance = (Button) findViewById(R.id.btnAddBalance);
        viewevent = (Button) findViewById(R.id.btnViewEvents);
        addcomplaint = (Button) findViewById(R.id.btnAddComplaint);
        logout = (Button) findViewById(R.id.btnUserLogout);


        /*IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.ne.feereminder.RestartSensor");
        getBaseContext().registerReceiver(br, intentFilter);*/

        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        filter.addAction(Intent.ACTION_TIME_CHANGED);

        //getDetails();

        addbalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UserHome.this,AddBalance.class);
                startActivity(i);
            }
        });

        maintainance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserHome.this, Maintainance.class);
                startActivity(i);
            }
        });

        viewevent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserHome.this,ViewEvents.class);
                startActivity(i);
            }
        });

        addcomplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserHome.this,AddComplaint.class);
                startActivity(i);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserHome.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                isp.editor.clear();
                isp.editor.commit();
                //sql.deleteAll();
                startActivity(i);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        Toast.makeText(UserHome.this, "Please Logout To Exit!", Toast.LENGTH_SHORT).show();
    }
}
