package com.example.gt_wallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;

public class RequestActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_SEND_SMS = 123;
    public EditText namereq, numreq, reqmessage, reqbal;

    public TextView balanceruser;
    public Button confirmreq, bckbtn;
    private ImageButton contactsbtn;
    public static final String URL_SHOW_BALANCE = "http://192.168.31.150:8080/GreatTreasure/Balanceshow.php";

    public static final String URL_REQUEST_Balance = "http://192.168.31.150:8080/GreatTreasure/RequestUser.php";

    private static final int RESULT_PICK_CONTACT1 = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        contactsbtn = findViewById(R.id.contactsbtn);
        namereq = findViewById(R.id.namereq);
        numreq = findViewById(R.id.numreq);
        reqbal = findViewById(R.id.reqbal);
        reqmessage = findViewById(R.id.messagereq);

        balanceruser = findViewById(R.id.balanceruser);


        confirmreq = findViewById(R.id.confirmreq);
        bckbtn = findViewById(R.id.bckbtn);

        bckbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
            }
        });
        contactsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT1);
            }
        });

        confirmreq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(RequestActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    // Permission not granted, request it
                    ActivityCompat.requestPermissions(RequestActivity.this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_SEND_SMS);
                    return;
                } else {
                    // Permission granted, send the SMS
                    String contact = numreq.getText().toString();
                    String message = reqmessage.getText().toString();
                    String name = namereq.getText().toString();
                    String balancereq = reqbal.getText().toString();

                    String valid ="";

                    if (name.equals("")){
                        valid = "Name is required";
                    }else if(balancereq.equals(""))
                    {
                        valid = "Enter amount";
                    }else if (message.equals(""))
                    {
                        valid = "Enter send message";
                    }

                    else
                    {
                        double amount = Double.parseDouble(balancereq);
                        if (amount > 20000)
                        {
                            valid = "Amount exceeds the limit";
                        }
                    }

                    if(!valid.isEmpty())
                    {
                        Toast.makeText(RequestActivity.this, valid, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    boolean smsSent = sendSms(contact, message);
                    if (!smsSent) {
                        // SMS sending failed, exit the code execution
                        return;
                    }else
                    {
                        Confirmform();
                    }

                }

            }
        });
        showbalance();
    }
    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_PICK_CONTACT1:
                    contactPicked1(data);
                    break;
            }
        } else {
            Log.e("SetupActivity", "Failed to pick contact");
        }
    }
    private void contactPicked1 (Intent data){
        Cursor cursor = null;
        try {
            String phoneNo = null;
            Uri uri = data.getData();
            cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            phoneNo = cursor.getString(phoneIndex);
            numreq.setText(phoneNo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //confirmation to request
    public void Confirmform(){
        final String contact = numreq.getText().toString();
        final String name = namereq.getText().toString();
        final String balancereq = reqbal.getText().toString();

        class confirmation extends AsyncTask<Void, Void, String>{
            ProgressDialog pdLoading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pdLoading = new ProgressDialog(RequestActivity.this);
                pdLoading.setMessage("\tLoading...");
                pdLoading.setCancelable(false);
                pdLoading.show();
            }

            @Override
            protected String doInBackground(Void... voids) {

                RequestHandler requestHandler = new RequestHandler();


                HashMap<String, String> params = new HashMap<>();

                String uid = getIntent().getStringExtra("uid");
                params.put("uid", uid);
                params.put("number", contact);
                params.put("name", name);
                params.put("balancereq", balancereq);
                return requestHandler.sendPostRequest(URL_REQUEST_Balance, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pdLoading.dismiss();

                try {
                    JSONObject obj = new JSONObject(s);

                    if (!obj.getBoolean("error")) {

                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        clear();
                    } else {
                        String message = obj.getString("message");
                        Toast.makeText(RequestActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(RequestActivity.this, "JSONException: " + e.getMessage() + s, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(RequestActivity.this, "Exception: " + e.getMessage() + s, Toast.LENGTH_SHORT).show();
                }
            }
        }
        confirmation confirm = new confirmation();
        confirm.execute();
    }

    //show balance
    public void showbalance()
    {
        class ShowBalanceTask extends AsyncTask<Void, Void, String> {
            ProgressDialog pdLoading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pdLoading = new ProgressDialog(RequestActivity.this);
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
                String uid = getIntent().getStringExtra("uid");
                // Use the uid value as needed
                params.put("uid", uid);
                return requestHandler.sendPostRequest(URL_SHOW_BALANCE, params);
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
                        balanceruser.setText(formattedBalance);
                    } else {
                        String message = obj.getString("message");
                        Toast.makeText(RequestActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(RequestActivity.this, "JSONException: " + e.getMessage() + s, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(RequestActivity.this, "Exception: " + e.getMessage() + s, Toast.LENGTH_SHORT).show();
                }
            }
        }

        ShowBalanceTask showBalanceTask = new ShowBalanceTask();
        showBalanceTask.execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_SEND_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                String num = numreq.getText().toString();
                String messreq = reqmessage.getText().toString();
                // Permission granted, send the SMS

                sendSms(num, messreq);
            } else {
                // Permission denied, show a message or handle it accordingly
                Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private boolean sendSms(String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            String name = namereq.getText().toString();
            String messreq = reqmessage.getText().toString();
            String reqbalance = reqbal.getText().toString();
            String msg = name + " Send Request Loan: "+ reqbalance + "\nmessage: " + messreq;
            smsManager.sendTextMessage(phoneNumber, null, msg, null, null);
            return true; // SMS sent successfully
        } catch (Exception e) {
            Toast.makeText(this, "Failed to send SMS.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false; // SMS sending failed
        }
    }

    public void clear()
    {
        namereq.setText("");
        numreq.setText("");
        reqbal.setText("");
        reqmessage.setText("");
        namereq.setText("");
    }

}