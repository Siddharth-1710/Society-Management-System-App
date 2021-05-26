package com.societymanagementsystem;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class AddEvents extends AppCompatActivity {

    EditText eventName;
    TextView startDate, startTime;
    Button addEvent;
    Spinner spinner;

    DatePickerDialog datePicker;
    int dateselector = 0;
    String pid = "";
    int diff=0;

    Calendar myCalendar = Calendar.getInstance();

    String sdate="",stime="none", ename="",society="";

    String addEventURL = IPaddress.ip+"addevent.php";
    String getSocietyURL = IPaddress.ip+"getSociety.php";

    List society_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_events);

        eventName = (EditText) findViewById(R.id.textEventName);
        startDate = (TextView) findViewById(R.id.textStartDate);
        startTime = (TextView) findViewById(R.id.textStartTime);
        addEvent = (Button) findViewById(R.id.bAddEvent);

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                society = parent.getItemAtPosition(position).toString();
                //Toast.makeText(AddEvents.this, society+" Society Is Selected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(AddEvents.this, "Please Select A Society!", Toast.LENGTH_SHORT).show();
            }
        });

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        getSociety();

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            dateselector = 1;
            datePicker = new DatePickerDialog(AddEvents.this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));
            datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePicker.show();
            }
        });

        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            final Calendar mcurrentTime = Calendar.getInstance();

            final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            final int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(AddEvents.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    startTime.setText(selectedHour + ":" + selectedMinute);
                    if(selectedMinute==0) {
                        stime = startTime.getText().toString() + "0";
                    }else{
                        stime = startTime.getText().toString();
                    }
                }
            }, hour, minute, true);//Yes 24 hour time
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();

            }
        });

        addEvent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ename = eventName.getText().toString().trim();

                if(ename.isEmpty() || stime.isEmpty() || sdate.isEmpty() || society.isEmpty()){
                    Toast.makeText(AddEvents.this, "Enter All Required Details!", Toast.LENGTH_SHORT).show();
                }else{
                    addEvent();
                }
            }
        });

    }

    private void getSociety() {

        RequestParams params = new RequestParams();
        params.put("admin", "1");

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(getSocietyURL, params, new AsyncHttpResponseHandler() {
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

                    ArrayAdapter a1 = new ArrayAdapter(AddEvents.this,android.R.layout.simple_spinner_dropdown_item, society_name);
                    spinner.setAdapter(a1);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(AddEvents.this, " Failed At Exception" + e, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                Toast.makeText(getApplicationContext(), "Connectivity Error" , Toast.LENGTH_LONG).show();
            }
        });

    }

    private void updateLabel() {
        String myFormat = "yyyy/MM/dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        sdate = sdf.format(myCalendar.getTime());
        startDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void addEvent(){

        RequestParams params = new RequestParams();
        params.put("ename",ename);
        params.put("society",society);
        params.put("sdate",sdate);
        params.put("stime",stime);

        final ProgressDialog pDialog = ProgressDialog.show(AddEvents.this,"Processing","please wait...",true,false);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(addEventURL, params, new AsyncHttpResponseHandler() {

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
                        Toast.makeText(AddEvents.this,message,Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(AddEvents.this, AdminHome.class);
                        startActivity(i);

                    }else{
                        pDialog.dismiss();
                        Toast.makeText(AddEvents.this,message,Toast.LENGTH_SHORT).show();

                    }
                }catch(JSONException e){
                    pDialog.dismiss();
                    Toast.makeText(AddEvents.this,"Error in JSON",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(AddEvents.this,"Connectivity failed with server! Try again.",Toast.LENGTH_LONG).show();
            }
        });
    }

}