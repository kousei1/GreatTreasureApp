package com.example.gt_wallet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
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
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;


public class SendActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_SEND_SMS = 123;

    private Button cnfm, cnlbtn;
    private ImageButton contactsbtb;
    private EditText sendnumberto, amounttoSent;

     public static final String sendCash = "http://192.168.31.150:8080/GreatTreasure/sendcash.php";

    // Declare a class-level constant for the request code
    private static final int RESULT_PICK_CONTACT1 = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        sendnumberto = findViewById(R.id.sendnumberto);
        amounttoSent = findViewById(R.id.amounttoSent);

        cnfm = findViewById(R.id.cnfm);
        cnlbtn = findViewById(R.id.cancelbtn);

        contactsbtb = findViewById(R.id.contactsbtn);

        cnfm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(SendActivity.this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    // Permission not granted, request it
                    ActivityCompat.requestPermissions(SendActivity.this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_SEND_SMS);
                    return;
                }else{
                    String contact = sendnumberto.getText().toString();
                    String amountsend = amounttoSent.getText().toString();

                    String errmsg = "";

                    if (contact.isEmpty())
                    {
                        errmsg = "Enter the number";
                    }else if (amountsend.isEmpty())
                    {
                        errmsg = "Enter amount to send";
                    }else {
                        double sendamount = Double.parseDouble(amountsend);

                        if (sendamount > 20000) {

                            errmsg = "can't send a large amount";
                        }
                    }

                    if(!errmsg.isEmpty())
                    {
                        Toast.makeText(SendActivity.this, errmsg, Toast.LENGTH_SHORT).show();
                    }

                    boolean smsSent = sendSms(contact, amountsend);
                    if (!smsSent) {
                        // SMS sending failed, exit the code execution
                        return;
                    }else {
                        sendMoneytask();
                        sendnumberto.setText("");
                        amounttoSent.setText("");
                    }

                }

            }
        });

        cnlbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uid = getIntent().getStringExtra("uid");
                Intent tent = getIntent();
                String num = tent.getStringExtra("num");
                Intent intent = new Intent(SendActivity.this, HomeActivity.class);
                intent.putExtra("uid", uid);
                intent.putExtra("number", num);
                startActivity(intent);
            }
        });

        contactsbtb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT1);
            }
        });


    }

    public void sendMoneytask(){
        final String money = amounttoSent.getText().toString();
        final String number = sendnumberto.getText().toString();
        class MoneyTask extends AsyncTask<Void, Void, String>{
            ProgressDialog pdLoading = new ProgressDialog(SendActivity.this);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                pdLoading.setMessage("\tLoading...");
                pdLoading.setCancelable(false);
                pdLoading.show();
            }

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                String uid = getIntent().getStringExtra("uid");
                HashMap<String, String> params = new HashMap<>();
                params.put("number", number);
                params.put("moneysend", money);
                params.put("sendID", uid);

                return requestHandler.sendPostRequest(sendCash, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pdLoading.dismiss();

                try {
                    JSONObject obj = new JSONObject(s);
                    if (!obj.getBoolean("error")) {
                        // Login successful, extract user data
                        Toast.makeText(SendActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SendActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(SendActivity.this, "Exception: " + e + s, Toast.LENGTH_LONG).show();
                }
            }

        }

        MoneyTask cashsend = new MoneyTask();
        cashsend.execute();

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
            sendnumberto.setText(phoneNo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_SEND_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                String num = sendnumberto.getText().toString();
                String messreq = amounttoSent.getText().toString();
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
            String amount = amounttoSent.getText().toString();
            Intent tent = getIntent();
            String num = tent.getStringExtra("num");
            String mess = "you receive: " + amount + " from " + num;
            smsManager.sendTextMessage(phoneNumber, null, mess, null, null);
            return true; // SMS sent successfully
        } catch (Exception e) {
            Toast.makeText(this, "Failed to send SMS.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false; // SMS sending failed
        }
    }






}