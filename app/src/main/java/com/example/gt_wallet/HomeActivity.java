package com.example.gt_wallet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {

    CardView treasuresend, treasureinbtn, requesttreasurebtn, outtreasurebtn, treasurehistory, accountbtn;

    TextView balanceuser, requestview;


    public static final String URL_SHOW_BALANCE = "http://192.168.31.150:8080/GreatTreasure/Balanceshow.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        balanceuser = findViewById(R.id.balanceuser);

        requestview = findViewById(R.id.requestview);


        treasuresend = findViewById(R.id.treasuresend);
        treasureinbtn = findViewById(R.id.treasureinbtn);
        requesttreasurebtn = findViewById(R.id.requesttreasurebtn);
        outtreasurebtn = findViewById(R.id.outtreasurebtn);
        treasurehistory = findViewById(R.id.treasurehistory);
        accountbtn = findViewById(R.id.accountbtn);



        requesttreasurebtn.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View view) {
                String uid = getIntent().getStringExtra("uid");

                Intent intent = new Intent(HomeActivity.this, RequestActivity.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });

        treasuresend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uid = getIntent().getStringExtra("uid");
                String num = getIntent().getStringExtra("number");//show the number of user request
                Intent intent = new Intent(HomeActivity.this, SendActivity.class);
                intent.putExtra("uid", uid);
                intent.putExtra("num", num);
                startActivity(intent);
            }
        });


        treasureinbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, CashInUser.class));
            }
        });


        outtreasurebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uid = getIntent().getStringExtra("uid");
                String num = getIntent().getStringExtra("number");//show the number of user request
                Intent intent = new Intent(HomeActivity.this, OutTreasureActivity.class);
                intent.putExtra("uid", uid);
                intent.putExtra("num", num);
                startActivity(intent);
            }
        });

        treasurehistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uid = getIntent().getStringExtra("uid");
                Intent intent = new Intent(HomeActivity.this, HistoryTreasureActivity.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });

        accountbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uid = getIntent().getStringExtra("uid");
                Intent intent = new Intent(HomeActivity.this, AccountActivity.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });

        showBalance();
    }

    private void showBalance() {
        class ShowBalanceTask extends AsyncTask<Void, Void, String> {
            ProgressDialog pdLoading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pdLoading = new ProgressDialog(HomeActivity.this);
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
                        balanceuser.setText(formattedBalance);//display


                        requestview.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String uid = getIntent().getStringExtra("uid");
                                String num = getIntent().getStringExtra("number");//show the number of user request
                                Intent intent = new Intent(HomeActivity.this, RequestView.class);
                                intent.putExtra("uid", uid);
                                intent.putExtra("number", num);
                                intent.putExtra("userbalance", formattedBalance);
                                startActivity(intent);
                            }
                        });
                    } else {
                        String message = obj.getString("message");
                        Toast.makeText(HomeActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(HomeActivity.this, "JSONException: " + e.getMessage() + s, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(HomeActivity.this, "Exception: " + e.getMessage() + s, Toast.LENGTH_SHORT).show();
                }
            }
        }
        ShowBalanceTask showBalanceTask = new ShowBalanceTask();
        showBalanceTask.execute();

    }



}