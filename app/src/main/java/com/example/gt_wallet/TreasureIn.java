package com.example.gt_wallet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;

public class TreasureIn extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_SEND_SMS = 123;
    private EditText usercashnumber, usercashamount;

    private Button confirmbtn, exitbtn;

    private String Sendsms, CashAmount;

    public static final String cashIn = "http://192.168.31.150:8080/GreatTreasure/sendcash.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treasure_in);

        usercashnumber = findViewById(R.id.usercashnumber);
        usercashamount = findViewById(R.id.usercashamount);

        confirmbtn = findViewById(R.id.confirmbtn);
        exitbtn = findViewById(R.id.exitbtn);

        confirmbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cashInUser();
            }
        });

        exitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }



    public void cashInUser() {
        final String cashnumber = usercashnumber.getText().toString();
        final String cashamount = usercashamount.getText().toString();

        Sendsms = cashnumber;
        CashAmount = cashamount;

        int withdrawlAmount = Integer.parseInt(cashamount);

        String msgdisplay = "";

        if (withdrawlAmount % 50 != 0)
        {
            msgdisplay = "Invalid amount";
        }else if (withdrawlAmount < 100)
        {
            msgdisplay = "minimum amount is 100";
        }

        if (!msgdisplay.isEmpty())
        {
            Toast.makeText(TreasureIn.this, msgdisplay, Toast.LENGTH_SHORT).show();
            return;
        }

        class cashInTask extends AsyncTask<Void, Void, String>{
            ProgressDialog pdLoading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pdLoading = new ProgressDialog(TreasureIn.this);
                pdLoading.setMessage("\tLoading...");
                pdLoading.setCancelable(false);
                pdLoading.show();
            }

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();

                // Retrieve user data from the Intent
                //To pass uid on php
                params.put("number", cashnumber);
                params.put("AmountIn", cashamount);
                return requestHandler.sendPostRequest(cashIn, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pdLoading.dismiss();
                try {
                    JSONObject obj = new JSONObject(s);

                    if (!obj.getBoolean("error")) {

                        if (ContextCompat.checkSelfPermission(TreasureIn.this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                            // Permission not granted, request it
                            ActivityCompat.requestPermissions(TreasureIn.this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_SEND_SMS);
                        } else {

                            sendSms(cashnumber);
                            usercashnumber.setText("");
                            usercashamount.setText("");
                        }




                    } else {
                        String message = obj.getString("message");
                        Toast.makeText(TreasureIn.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(TreasureIn.this, "JSONException: " + e.getMessage() + s, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(TreasureIn.this, "Exception: " + e.getMessage() + s, Toast.LENGTH_SHORT).show();
                }
            }

        }

        cashInTask cashin = new cashInTask();
        cashin.execute();

    }



    private boolean sendSms(String phoneNumber) {
        try {
            SmsManager smsManager = SmsManager.getDefault();

            String loader = "Loader ";
            String msg = Sendsms + " You receive cash In from: " + loader + "\nAmount: " + CashAmount;
            smsManager.sendTextMessage(phoneNumber, null, msg, null, null);
            return true; // SMS sent successfully
        } catch (Exception e) {
            Toast.makeText(this, "Failed to send SMS.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false; // SMS sending failed
        }
    }

}