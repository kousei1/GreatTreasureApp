package com.example.gt_wallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class RequestView extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_SEND_SMS = 123;

    public static final String requestview_show = "http://192.168.31.150:8080/GreatTreasure/viewReq.php";
    public static final String accept_dec_req = "http://192.168.31.150:8080/GreatTreasure/viewReq.php";

    private TableLayout reqlist;

    public String balancerequest, numbalance;

    public EditText searchnumbertxt;

    private Button cncl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_view);
        cncl = findViewById(R.id.cncl);
        reqlist = findViewById(R.id.reqlist);

        searchnumbertxt = findViewById(R.id.searchnumbertxt);


        searchnumbertxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                String search = searchnumbertxt.getText().toString();
                searchrequest(search);
                return false;
            }
        });

        cncl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uid = getIntent().getStringExtra("uid");
                String num = getIntent().getStringExtra("number");//show the number of user request
                Intent intent = new Intent(RequestView.this, HomeActivity.class);
                intent.putExtra("uid", uid);
                intent.putExtra("number", num);
                startActivity(intent);
            }
        });

        requestviews();
    }


    private void searchrequest(String searchTxt){

        class searchReq extends AsyncTask<Void, Void, String>{

            ProgressDialog pdLoading = new ProgressDialog(RequestView.this);

            String searchTxt;

            public searchReq(String searchTxt){
                this.searchTxt = searchTxt;
            }

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
                String num = getIntent().getStringExtra("number");//show the number of user request
                params.put("num", num);
                params.put("searchReq", searchTxt);
                return requestHandler.sendPostRequest(requestview_show, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pdLoading.dismiss();
                reqlist.removeAllViews();
                try {
                    JSONObject obj = new JSONObject(s);
                    if (!obj.getBoolean("error")) {
                        JSONArray dataArray = obj.getJSONArray("data");
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject dataObj = dataArray.getJSONObject(i);
                            String date = dataObj.getString("date");
                            String balance = dataObj.getString("BalanceReq");
                            String contact = dataObj.getString("number");
                            String reqID = dataObj.getString("reqID");

                            balancerequest = balance; //get the BalanceReq

                            // Create a new TableRow and TextViews for the table
                            TableRow idreq = new TableRow(RequestView.this);
                            TableRow rbalance = new TableRow(RequestView.this);
                            TableRow actionrow = new TableRow(RequestView.this);

                            TextView viewdate = new TextView(RequestView.this);
                            viewdate.setPadding(15, 12, 8, 12);
                            viewdate.setText("Date: " + date);

                            TextView reqbal = new TextView(RequestView.this);
                            reqbal.setPadding(50,0,0,0);
                            reqbal.setText("Loan: " + balance);

                            TextView contactview = new TextView(RequestView.this);
                            contactview.setPadding(75,0,0,0);
                            contactview.setText("Contact: " + contact);


                            TextView actionview = new TextView(RequestView.this);
                            actionview.setPadding(15,30,70,30);
                            actionview.setText("Accept");

                            TextView decline = new TextView(RequestView.this);
                            decline.setPadding(80,30,0,30);
                            decline.setText("Decline");

                            // Create a horizontal line view
                            View horizontalLine = new View(RequestView.this);
                            horizontalLine.setBackgroundColor(Color.BLACK);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    3 // Set the desired height of the horizontal line
                            );
                            params.setMargins(0, 0, 0, 8); // Add bottom margin to adjust spacing
                            horizontalLine.setLayoutParams(params);

                            //Get the ID
                            final int id = Integer.parseInt(reqID);

                            decline.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    declineRow(id);
                                    // Retrieve the parent TableRow and remove it from the TableLayout
                                    TableRow parentRow = (TableRow) view.getParent();
                                    ((TableLayout) parentRow.getParent()).removeView(parentRow);
                                }
                            });

                            actionview.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    acceptedRow(id);
                                    TableRow parentRow = (TableRow) view.getParent();
                                    ((TableLayout) parentRow.getParent()).removeView(parentRow);
                                }
                            });

                            // Add TextViews to the TableRow
                            idreq.addView(viewdate);
                            rbalance.addView(contactview);
                            rbalance.addView(reqbal);
                            actionrow.addView(decline);
                            actionrow.addView(actionview);


                            reqlist.addView(horizontalLine);
                            // Add the TableRow to the TableLayout
                            reqlist.addView(idreq);
                            reqlist.addView(rbalance);
                            reqlist.addView(actionrow);
                        }
                    } else {
                        Toast.makeText(RequestView.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(RequestView.this, "Exception: " + e, Toast.LENGTH_LONG).show();
                }
            }

        }
        searchReq searchReq = new searchReq(searchTxt);
        searchReq.execute();

    }



    private void requestviews() {

        class ViewRequestUser extends AsyncTask<Void, Void, String> {
            ProgressDialog pdLoading = new ProgressDialog(RequestView.this);

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
                String num = getIntent().getStringExtra("number");//show the number of user request
                params.put("num", num);
                return requestHandler.sendPostRequest(requestview_show, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pdLoading.dismiss();
                reqlist.removeAllViews();
                try {
                    JSONObject obj = new JSONObject(s);
                    if (!obj.getBoolean("error")) {
                        JSONArray dataArray = obj.getJSONArray("data");
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject dataObj = dataArray.getJSONObject(i);
                            String date = dataObj.getString("date");
                            String balance = dataObj.getString("BalanceReq");
                            String contact = dataObj.getString("number");
                            String reqID = dataObj.getString("reqID");

                            balancerequest = balance; //get the BalanceReq
                            numbalance = contact;

                            // Create a new TableRow and TextViews for the table
                            TableRow idreq = new TableRow(RequestView.this);
                            TableRow rbalance = new TableRow(RequestView.this);
                            TableRow actionrow = new TableRow(RequestView.this);

                            TextView viewdate = new TextView(RequestView.this);
                            viewdate.setPadding(15, 12, 8, 12);
                            viewdate.setText("Date: " + date);

                            TextView reqbal = new TextView(RequestView.this);
                            reqbal.setPadding(50,0,0,0);
                            reqbal.setText("Loan: " + balance);

                            TextView contactview = new TextView(RequestView.this);
                            contactview.setPadding(75,0,0,0);
                            contactview.setText("Contact: " + contact);


                            TextView actionview = new TextView(RequestView.this);
                            actionview.setPadding(15,30,70,0);
                            actionview.setText("Accept");

                            TextView decline = new TextView(RequestView.this);
                            decline.setPadding(80,30,0,0);
                            decline.setText("Decline");

                            // Create a horizontal line view
                            View horizontalLine = new View(RequestView.this);
                            horizontalLine.setBackgroundColor(Color.BLACK);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    3 // Set the desired height of the horizontal line
                            );
                            params.setMargins(0, 0, 0, 8); // Add bottom margin to adjust spacing
                            horizontalLine.setLayoutParams(params);

                            //Get the ID
                            final int id = Integer.parseInt(reqID);

                            decline.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    declineRow(id);
                                    // Retrieve the parent TableRow and remove it from the TableLayout
                                    TableRow parentRow = (TableRow) view.getParent();
                                    ((TableLayout) parentRow.getParent()).removeView(parentRow);
                                }
                            });

                            actionview.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    acceptedRow(id);
                                    TableRow parentRow = (TableRow) view.getParent();
                                    ((TableLayout) parentRow.getParent()).removeView(parentRow);
                                }
                            });

                            // Add TextViews to the TableRow
                            idreq.addView(viewdate);
                            rbalance.addView(contactview);
                            rbalance.addView(reqbal);
                            actionrow.addView(decline);
                            actionrow.addView(actionview);


                            reqlist.addView(horizontalLine);
                            // Add the TableRow to the TableLayout
                            reqlist.addView(idreq);
                            reqlist.addView(rbalance);
                            reqlist.addView(actionrow);
                        }
                    } else {
                        Toast.makeText(RequestView.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(RequestView.this, "Exception: " + e + s, Toast.LENGTH_LONG).show();
                }
            }
        }

        ViewRequestUser reqsight = new ViewRequestUser();
        reqsight.execute();
    }



    //declined user request
    public void declineRow(int id)
    {
        class decline extends AsyncTask<Void, Void, String> {
            ProgressDialog pdLoading = new ProgressDialog(RequestView.this);
            int id;
            public decline(int id) {
                this.id = id;
            }
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
                params.put("uidReq", String.valueOf(id));
                return requestHandler.sendPostRequest(accept_dec_req, params);
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pdLoading.dismiss();

                try {
                    if (!s.isEmpty()) {
                        JSONObject obj = new JSONObject(s);
                        if (!obj.getBoolean("error")) {
                            // Deletion successful
                            if (ContextCompat.checkSelfPermission(RequestView.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                                // Permission not granted, request it
                                ActivityCompat.requestPermissions(RequestView.this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_SEND_SMS);
                            } else {

                                sendSms1(numbalance, balancerequest);
                                recreate();
                            }

                        } else {
                            Toast.makeText(RequestView.this, obj.getString("message"), Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        Toast.makeText(RequestView.this, "Empty response received from the server", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(RequestView.this, "JSONException: " + e.getMessage() + s, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                } catch (Exception e) {
                    Toast.makeText(RequestView.this, "Exception: " + e.getMessage() + s, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

        }
        decline deny = new decline(id);
        deny.execute();
    }


    // RequestView activity
    public void acceptedRow(int id) {
        class Acceptance extends AsyncTask<Void, Void, String> {
            ProgressDialog pdLoading = new ProgressDialog(RequestView.this);
            int id;

            public Acceptance(int id) {
                this.id = id;
            }

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
                params.put("acceptReqID", String.valueOf(id));
                params.put("IDUID", uid);
                params.put("userbalance", balancerequest);
                return requestHandler.sendPostRequest(accept_dec_req, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (pdLoading.isShowing()) {
                    pdLoading.dismiss();
                }
                try {
                    if (!s.isEmpty()) {
                        JSONObject obj = new JSONObject(s);
                        if (!obj.getBoolean("error")) {
                            if (ContextCompat.checkSelfPermission(RequestView.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                                // Permission not granted, request it
                                ActivityCompat.requestPermissions(RequestView.this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_SEND_SMS);
                            } else {
                                Toast.makeText(RequestView.this, obj.getString("message"), Toast.LENGTH_SHORT).show();

                                sendSms(numbalance, balancerequest);
                                recreate();
                            }
                        } else {
                            recreate();
                            Toast.makeText(RequestView.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(RequestView.this, "Empty response received from the server" + s, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(RequestView.this, "JSONException: " + e.getMessage() + s, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                } catch (Exception e) {
                    Toast.makeText(RequestView.this, "Exception: " + e.getMessage() + s, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }

        Acceptance accept = new Acceptance(id);
        accept.execute();
    }




    private boolean sendSms(String phoneNumber, String balanceRequest) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            String num = getIntent().getStringExtra("number");
            String msg = num + " Accepted Request Loan: " + balanceRequest ;
            smsManager.sendTextMessage(phoneNumber, null, msg, null, null);
            return true; // SMS sent successfully
        } catch (Exception e) {
            Toast.makeText(this, "Failed to send SMS.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false; // SMS sending failed
        }
    }

    private boolean sendSms1(String phoneNumber, String balanceRequest) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            String num = getIntent().getStringExtra("number");
            String msg = num + " Declined Request Loan: " + balanceRequest;
            smsManager.sendTextMessage(phoneNumber, null, msg, null, null);
            return true; // SMS sent successfully
        } catch (Exception e) {
            Toast.makeText(this, "Failed to send SMS.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false; // SMS sending failed
        }
    }



}