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

public class OutTreasureActivity extends AppCompatActivity {

    private TextView displayamount;
    private EditText amountwithdraw;
    private Button withdrawbtn, cancelbtn;

    public static final String balanceuser = "http://192.168.31.150:8080/GreatTreasure/balanceinnoutshow.php";
    public static final String cashout = "http://192.168.31.150:8080/GreatTreasure/cashoutuser.php";
    private static final int PERMISSION_REQUEST_SEND_SMS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_treasure);

        displayamount = findViewById(R.id.displayamount);


        amountwithdraw = findViewById(R.id.amountouttxt);

        cancelbtn = findViewById(R.id.cancelbtn);
        withdrawbtn = findViewById(R.id.withdrawbtn);

        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uid = getIntent().getStringExtra("uid");
                Intent tent = getIntent();
                String num = tent.getStringExtra("num");
                Intent intent = new Intent(OutTreasureActivity.this, HomeActivity.class);
                intent.putExtra("uid", uid);
                intent.putExtra("number", num);
                startActivity(intent);
            }
        });

        withdrawbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                withdrawCash();
            }
        });


        showBalance();



    }
//show the balance of user
    public void showBalance()
    {
        class balshow extends AsyncTask<Void, Void, String>
        {
            ProgressDialog pdLoading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pdLoading = new ProgressDialog(OutTreasureActivity.this);
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
                String uid = getIntent().getStringExtra("uid");//Get the data from uid
                params.put("uid", String.valueOf(uid)); // Use the uid value in your code
                return requestHandler.sendPostRequest(balanceuser, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pdLoading.dismiss();
                try {
                    JSONObject obj = new JSONObject(s);

                    if (!obj.getBoolean("error")) {
                        String balance = obj.getString("balance");
                        double balanceValue = Double.parseDouble(balance);

                        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
                        String formattedBalance = decimalFormat.format(balanceValue);
                        displayamount.setText(formattedBalance);//display
                    } else {
                        Toast.makeText(OutTreasureActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(OutTreasureActivity.this, "JSONException: " + e.getMessage() + s, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(OutTreasureActivity.this, "Exception: " + e.getMessage() + s, Toast.LENGTH_SHORT).show();
                }
            }
        }
        balshow cashout = new balshow();
        cashout.execute();
    }

    //cash out

    public void withdrawCash()
    {
        final String moneywithdraw = amountwithdraw.getText().toString();


       double withdrawlAmount = Double.parseDouble(moneywithdraw);

       String msgdisplay = "";

       if (withdrawlAmount % 50 != 0)
       {
           msgdisplay = "Invalid amount";
       }else if (withdrawlAmount < 100)
       {
           msgdisplay = "minimum amount is 100";
       }else if (withdrawlAmount > 50000)
       {
           msgdisplay = "Reach the maximum of withdrawal";
       }

       if (!msgdisplay.isEmpty())
       {
           Toast.makeText(OutTreasureActivity.this, msgdisplay, Toast.LENGTH_SHORT).show();
           return;
       }

        class userWithdraw extends AsyncTask<Void, Void, String>{

            ProgressDialog pdLoading;

            @Override






            
            protected void onPreExecute() {
                super.onPreExecute();
                pdLoading = new ProgressDialog(OutTreasureActivity.this);
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
                String uid = getIntent().getStringExtra("uid");//Get the data from uid
                params.put("uid", String.valueOf(uid)); // Use the uid value in your code
                params.put("balancewithdraw", moneywithdraw);
                return requestHandler.sendPostRequest(cashout, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pdLoading.dismiss();
                try {
                    JSONObject obj = new JSONObject(s);

                    if (!obj.getBoolean("error")) {
                        if (ContextCompat.checkSelfPermission(OutTreasureActivity.this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                            // Permission not granted, request it
                            ActivityCompat.requestPermissions(OutTreasureActivity.this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_SEND_SMS);
                        }else{

                            Toast.makeText(OutTreasureActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            Intent tent = getIntent();
                            String num = tent.getStringExtra("num");
                            sendSms(num);
                            recreate();

                        }

                    } else {
                        Toast.makeText(OutTreasureActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(OutTreasureActivity.this, "JSONException: " + e.getMessage() + s, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(OutTreasureActivity.this, "Exception: " + e.getMessage() + s, Toast.LENGTH_SHORT).show();
                }
            }


        }
        userWithdraw userwithdraw = new userWithdraw();
        userwithdraw.execute();

    }

    private boolean sendSms(String phoneNumber) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            String moneywithdraw = amountwithdraw.getText().toString();
            String msg = "You cash out amount: " + moneywithdraw + "\nPlease check your account for the remaining balance" ;
            smsManager.sendTextMessage(phoneNumber, null, msg, null, null);
            return true; // SMS sent successfully
        } catch (Exception e) {
            Toast.makeText(this, "Failed to send SMS.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false; // SMS sending failed
        }
    }












}