package com.societymanagementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddBalance extends AppCompatActivity {

    TextView amount;
    EditText cardno,cvvno,otpno,paymentamount;
    Button pay;
    String card="",cvv="",otp="";
    int amt=0;
    String paymentURL = IPaddress.ip+"addbalance.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_balance);

        amount = (TextView) findViewById(R.id.textTotalAmount);
        cardno = (EditText) findViewById(R.id.textCardNo);
        cvvno = (EditText) findViewById(R.id.editCVVno);
        pay = (Button) findViewById(R.id.btnPayment);
        paymentamount = (EditText) findViewById(R.id.textTotalAmountPayingNow);

        amount.setText("Total Amount : "+UserData.balance);

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                card = cardno.getText().toString().trim();
                cvv = cvvno.getText().toString().trim();

                //otp = otpno.getText().toString().trim();
                try {
                    amt = Integer.parseInt(paymentamount.getText().toString().trim());
                    if(amt!=0) {
                        if (!card.isEmpty() && !cvv.isEmpty()) {
                            if (card.length() == 12 && cvv.length() == 3) {
                                UserData.crd = card;

                                    UserData.amountpaying = amt;
                                    Intent i = new Intent(AddBalance.this, Verification.class);
                                    startActivity(i);

                            }
                        } else {
                            Toast toast = Toast.makeText(AddBalance.this, "Enter card no and cvv!", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                        }
                    }else{
                        Toast toast = Toast.makeText(AddBalance.this, "Please enter valid amount!", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    }
                }catch(Exception e){
                    Toast toast = Toast.makeText(AddBalance.this, "Enter correct amount!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }

            }
        });
    }
}
