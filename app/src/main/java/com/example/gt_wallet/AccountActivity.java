package com.example.gt_wallet;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class AccountActivity extends AppCompatActivity {

    TextView chngebtn, userlogoutbtn, nameofuser, contactofuser;

    EditText currentpass,passtxt,passtext2;

    Button confirmbtn, bckbtn;

    CardView card;

    public static final String user_forgot = "http://192.168.31.150:8080/GreatTreasure/changepassword.php";

    public static final String show_info = "http://192.168.31.150:8080/GreatTreasure/showinfo.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        nameofuser = findViewById(R.id.nameofuser);
        contactofuser = findViewById(R.id.contactofuser);

        userlogoutbtn = findViewById(R.id.userlogoutbtn);

        currentpass = findViewById(R.id.currentpass);
        passtxt = findViewById(R.id.passtxt);
        passtext2 = findViewById(R.id.passtxt2);

        confirmbtn = findViewById(R.id.confirmbtn);
        bckbtn = findViewById(R.id.bckbtn);

        chngebtn = findViewById(R.id.changePasswordButton);
        card = findViewById(R.id.card);


        chngebtn.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.baseline_keyboard_arrow_down_24, 0);

        chngebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(card.getVisibility() == View.VISIBLE)
                {
                    card.setVisibility(View.GONE);
                    chngebtn.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.baseline_keyboard_arrow_down_24, 0);
                }else
                {
                    card.setVisibility(View.VISIBLE);
                    chngebtn.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.baseline_keyboard_arrow_up_24, 0);
                }
            }
        });


        userlogoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmationDialog();
            }
        });

        confirmbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changepassword();
                currentpass.setText("");
                passtxt.setText("");
                passtext2.setText("");
            }
        });

        bckbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        displayinfo();

    }

    public void changepassword(){
        final String password = passtxt.getText().toString();
        final String passworconfirm = passtext2.getText().toString();
        final String currentpassword = currentpass.getText().toString();
        String validation = "";
        if (!password.equals(passworconfirm))
        {
            validation = "Password doesn't match";
        }

        if (!validation.isEmpty())
        {
            Toast.makeText(AccountActivity.this, validation, Toast.LENGTH_SHORT).show();
            return;
        }

        class passwordchange extends AsyncTask<Void, Void, String>{
            ProgressDialog pdLoading = new ProgressDialog(AccountActivity.this);

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

                HashMap<String, String> params = new HashMap<>();
                String uid = getIntent().getStringExtra("uid");
                params.put("uid", uid);
                params.put("password", password);
                params.put("cpassword", currentpassword);

                return requestHandler.sendPostRequest(user_forgot, params);
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pdLoading.dismiss();

                try {
                    if (!s.isEmpty()) {
                        JSONObject obj = new JSONObject(s);
                        if (!obj.getBoolean("error")) {
                            // Login successful, extract user data
                            Toast.makeText(AccountActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AccountActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AccountActivity.this, "Empty response received from the server", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(AccountActivity.this, "JSONException: " + e.getMessage() + s, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                } catch (Exception e) {
                    Toast.makeText(AccountActivity.this, "Exception: " + e.getMessage() +s, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

        }
        passwordchange changepass = new passwordchange();
        changepass.execute();

    }


    public void displayinfo(){

        class showinfo extends AsyncTask<Void, Void, String> {
            ProgressDialog pdLoading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pdLoading = new ProgressDialog(AccountActivity.this);
                pdLoading.setMessage("\tLoading...");
                pdLoading.setCancelable(false);
                pdLoading.show();
            }
//debug nalang to bukas!
            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();

                // Retrieve user data from the Intent
                //To pass uid on php
                String uid = getIntent().getStringExtra("uid");
                // Use the uid value as needed
                params.put("uid", uid);
                return requestHandler.sendPostRequest(show_info, params);
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pdLoading.dismiss();

                try {
                    JSONObject obj = new JSONObject(s);

                    if (!obj.getBoolean("error")) {
                        String name = obj.getString("name");
                        String contact = obj.getString("number");
                        nameofuser.setText(name);
                        contactofuser.setText(contact);
                    } else {
                        String message = obj.getString("message");
                        Toast.makeText(AccountActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AccountActivity.this, "JSONException: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(AccountActivity.this, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        }
        showinfo infoshow = new showinfo();
        infoshow.execute();

    }


    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("GreatTreasure");
        builder.setMessage("Are you sure you want to logout?");

        // Add the "OK" button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked "OK"
                exitApplication();
            }
        });

        // Add the "Cancel" button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked "Cancel"
                dialog.dismiss(); // Dismiss the dialog
            }
        });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void exitApplication() {
        Toast.makeText(AccountActivity.this, "Logging out", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(AccountActivity.this, MainActivity.class));
        finish();
    }


}