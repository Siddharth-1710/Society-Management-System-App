package com.societymanagementsystem;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Gravity;
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

import javax.mail.MessagingException;

import cz.msebera.android.httpclient.Header;

public class UserRegistration extends AppCompatActivity {

    EditText uname,umobile,uaddress,ucity,upincode,uemail,ucardno,upassword,uemergency;
    Button submit;
    String name,mob,address,city,pin,email,cardno,password,emergencyno;
    String registerURL = IPaddress.ip+"uregister.php";
    String getSocietyURL = IPaddress.ip+"getSociety.php";
    Spinner spinner;
    List society_name;
    String society;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        uname = (EditText) findViewById(R.id.editRegName);
        umobile = (EditText) findViewById(R.id.editRegMobileNo);
        uemergency = (EditText) findViewById(R.id.editRegEmergencyNo);
        uaddress = (EditText) findViewById(R.id.editRegAddress);
        ucity = (EditText) findViewById(R.id.editRegCity);
        upincode = (EditText) findViewById(R.id.editRegPincode);
        uemail = (EditText) findViewById(R.id.editRegEmail);
        ucardno = (EditText) findViewById(R.id.editRegCardNo);
        upassword = (EditText) findViewById(R.id.editRegPassword);
        submit = (Button) findViewById(R.id.btnRegSubmit);

      spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                society = parent.getItemAtPosition(position).toString();
                //Toast.makeText(UserRegistration.this, society+" Society Is Selected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(UserRegistration.this, "Please Select A Society!", Toast.LENGTH_SHORT).show();
            }
        });
        getSociety();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = uname.getText().toString().trim();
                mob = umobile.getText().toString().trim();
                emergencyno = uemergency.getText().toString().trim();
                address = uaddress.getText().toString().trim();
                city = ucity.getText().toString().trim();
                pin = upincode.getText().toString().trim();
                email = uemail.getText().toString().trim();
                cardno = ucardno.getText().toString().trim();
                password = upassword.getText().toString().trim();

                if(name==""||mob==""||address==""||city==""||pin==""||email==""||cardno==""||password==""||emergencyno==""||society==""){
                    Toast toast = Toast.makeText(UserRegistration.this,"Please enter all information",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();

                }else{
                    register();
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

                    ArrayAdapter a1 = new ArrayAdapter(UserRegistration.this,android.R.layout.simple_spinner_dropdown_item, society_name);
                    spinner.setAdapter(a1);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(UserRegistration.this, " Failed At Exception" + e, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                Toast.makeText(getApplicationContext(), "Connectivity Error" , Toast.LENGTH_LONG).show();
            }
        });

    }







    public void register(){
        RequestParams params = new RequestParams();
        params.put("name",name);
        params.put("password",password);
        params.put("mobile",mob);
        params.put("emergencyno",emergencyno);
        params.put("address",address);
        params.put("city",city);
        params.put("pincode",pin);
        params.put("email",email);
        params.put("cardno",cardno);
        params.put("society",society);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(registerURL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                System.out.print(response);

                try {
                    JSONObject obj = new JSONObject(response);
                    String message = obj.getString("message");
                    String success = obj.getString("success");

                    if(success.equals("200")){
                        Toast toast =Toast.makeText(UserRegistration.this,message,Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String message = "You Have Successfully Registered To Society Management System";
                                String[] files= {};
                                SendMailSSL sm = new SendMailSSL();
                                try {
                                    sm.sendEmailWithAttachments("smtp.gmail.com", "587", "mynewjava@gmail.com", "myjavarocking", email, "Registered Successfully!",message,files);
                                } catch (MessagingException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                        Intent i = new Intent(UserRegistration.this,MainActivity.class);
                        startActivity(i);
                        finish();


                    }else{
                        Toast toast = Toast.makeText(UserRegistration.this,message,Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();

                    }
                }catch(JSONException e){
                    Toast toast = Toast.makeText(UserRegistration.this,"Error in JSON",Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast toast = Toast.makeText(UserRegistration.this,"Connectivity failed with server! Try again.",Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
        });
    }
}
