package com.example.gt_wallet;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class HistoryTreasureActivity extends AppCompatActivity {

    private TableLayout historytrans;

    private TableRow hdate, headertitle, haction;
    //View
    private TextView hhdatentime, hhtitle, hhAmount,  deleteR, viewdetails, startDatetxt;

    private EditText searchtxt;
    private Button backbtn, datePickerButton;

    String selectedDate = "";

    public static final String historyfunction = "http://192.168.31.150:8080/GreatTreasure/TransactionHistory.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_treasure);
        backbtn = findViewById(R.id.backbtn);
        //header
        hhdatentime = findViewById(R.id.hhdatentime);
        hhtitle = findViewById(R.id.hhtitle);
        deleteR = findViewById(R.id.deleteR);
        hhAmount = findViewById(R.id.hhAmount);

        searchtxt = (EditText) findViewById(R.id.searchtxt);

        datePickerButton = findViewById(R.id.datePickerButton);

        startDatetxt = findViewById(R.id.startDatetxt);
        searchtxt.clearFocus();

//
        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get current date
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                // Create a DatePickerDialog and set the initial date to the current date
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        HistoryTreasureActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                // The selected date will be returned in the onDateSet callback
                                // Format the selected date to "Day, Month, Year" format
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                Calendar selectedCalendar = Calendar.getInstance();
                                selectedCalendar.set(Calendar.YEAR, year);
                                selectedCalendar.set(Calendar.MONTH, month);
                                selectedCalendar.set(Calendar.DAY_OF_MONTH, day);
                                selectedDate = sdf.format(selectedCalendar.getTime());
                                startDatetxt.setText(selectedDate);
                                // Update your UI or perform any other desired action with the selected date
                                showhistory();
                                searchtxt.setText("");

                            }
                        },
                        year,
                        month,
                        day
                );

                // Show the date picker dialog
                datePickerDialog.show();
            }

        });



        searchtxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

                String searchText = searchtxt.getText().toString();
                searchinfotask(searchText);
                startDatetxt.setText("");
                return false;
            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //row list
        historytrans = findViewById(R.id.historytrans);
        showhistory();
    }



    public void searchinfotask(String searchtxt) {
        class SearchInfoTask extends AsyncTask<Void, Void, String> {
            ProgressDialog pdLoading;
            String searchtxt;

            public SearchInfoTask(String searchtxt) {
                this.searchtxt = searchtxt;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pdLoading = new ProgressDialog(HistoryTreasureActivity.this);
                pdLoading.setMessage("\tLoading...");
                pdLoading.setCancelable(false);
                pdLoading.show();
            }

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();

                // Retrieve user data from the Intent
                // To pass uid on php
                String uid = getIntent().getStringExtra("uid"); // Get the data from uid
                params.put("uid", uid); // Use the uid value in your code
                params.put("searchview", searchtxt);
                return requestHandler.sendPostRequest(historyfunction, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pdLoading.dismiss();
                historytrans.removeAllViews();
                try {
                    JSONObject obj = new JSONObject(s);

                    if (!obj.getBoolean("error")) {
                        JSONArray dataArray = obj.getJSONArray("data");
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject dataObj = dataArray.getJSONObject(i);
                            String date = dataObj.getString("Date");
                            String trans_types = dataObj.getString("trans_types");
                            String getact = dataObj.getString("transh_ID");
                            String getTransID = dataObj.getString("trans_ID");
                            String getTotal = dataObj.getString("Trans_Total");

                            // Create a new TableRow and TextViews for the table
                            hdate = new TableRow(HistoryTreasureActivity.this);
                            headertitle = new TableRow(HistoryTreasureActivity.this);
                            haction = new TableRow(HistoryTreasureActivity.this);

                            hhdatentime = new TextView(HistoryTreasureActivity.this);
                            hhdatentime.setPadding(8, 8, 8, 25);
                            hhdatentime.setSingleLine(true);
                            hhdatentime.setText(date);

                            hhtitle = new TextView(HistoryTreasureActivity.this);
                            hhtitle.setPadding(50, 3, 50, 25);
                            hhtitle.setText(trans_types);

                            hhAmount = new TextView(HistoryTreasureActivity.this);
                            hhAmount.setPadding(20, 3, 30, 25);
                            hhAmount.setText(getTotal);

                            deleteR = new TextView(HistoryTreasureActivity.this);
                            deleteR.setPadding(100, 20, 0, 50);
                            deleteR.setText("Delete");

                            viewdetails = new TextView(HistoryTreasureActivity.this);
                            viewdetails.setPadding(100, 20, 0, 0);
                            viewdetails.setText("View");

                            // Create a horizontal line view
                            View horizontalLine = new View(HistoryTreasureActivity.this);
                            horizontalLine.setBackgroundColor(Color.BLACK);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    3 // Set the desired height of the horizontal line
                            );
                            params.setMargins(0, 0, 0, 8); // Add bottom margin to adjust spacing
                            horizontalLine.setLayoutParams(params);

                            // Get the ID
                            final int id = Integer.parseInt(getact);
                            final int HistoryID = Integer.parseInt(getTransID);

                            // Delete this
                            deleteR.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    deleteRow(id);

                                    // Retrieve the parent TableRow and remove it from the TableLayout
                                    TableRow parentRow = (TableRow) view.getParent();
                                    ((TableLayout) parentRow.getParent()).removeView(parentRow);
                                }
                            });

                            viewdetails.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    showAlertDialog(HistoryID);
                                }
                            });

                            hdate.addView(hhdatentime);
                            // Add TextViews to the TableRow
                            headertitle.addView(hhtitle);
                            headertitle.addView(hhAmount);

                            haction.addView(deleteR);
                            haction.addView(viewdetails);

                            // Add the horizontal line view below the TableRow
                            historytrans.addView(horizontalLine);

                            // Add the TableRows to the TableLayout
                            historytrans.addView(hdate);
                            historytrans.addView(headertitle);
                            historytrans.addView(haction);
                        }
                    } else {
                        String message = obj.getString("message");
                        Toast.makeText(HistoryTreasureActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(HistoryTreasureActivity.this, "JSONException: " + e.getMessage() + s, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(HistoryTreasureActivity.this, "Exception: " + e.getMessage() + s, Toast.LENGTH_SHORT).show();
                }
            }
        }

        SearchInfoTask searchTask = new SearchInfoTask(searchtxt);
        searchTask.execute();
    }


    public void showhistory(){
        final String startdate = startDatetxt.getText().toString();


        class historyoftrans extends AsyncTask<Void, Void, String>{
            ProgressDialog pdLoading;



            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pdLoading = new ProgressDialog(HistoryTreasureActivity.this);
                pdLoading.setMessage("\tLoading...");
                pdLoading.setCancelable(false);
                pdLoading.show();
            }

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();

                // Retrieve user data from the Intent
                // To pass uid on php
                String uid = getIntent().getStringExtra("uid"); // Get the data from uid
                params.put("uid", uid); // Use the uid value in your code
                params.put("startDate", startdate);
                return requestHandler.sendPostRequest(historyfunction, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pdLoading.dismiss();
                historytrans.removeAllViews();
                try {
                    JSONObject obj = new JSONObject(s);

                    if (!obj.getBoolean("error")) {
                        JSONArray dataArray = obj.getJSONArray("data");
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject dataObj = dataArray.getJSONObject(i);
                            String date = dataObj.getString("Date");
                            String trans_types = dataObj.getString("trans_types");
                            String getact = dataObj.getString("transh_ID");
                            String getTransID = dataObj.getString("trans_ID");
                            String getTotal = dataObj.getString("Trans_Total");

                            // Create a new TableRow and TextViews for the table
                            hdate = new TableRow(HistoryTreasureActivity.this);
                            headertitle = new TableRow(HistoryTreasureActivity.this);
                            haction = new TableRow(HistoryTreasureActivity.this);


                            hhdatentime = new TextView(HistoryTreasureActivity.this);
                            hhdatentime.setPadding(8, 12, 8, 25);
                            hhdatentime.setSingleLine(true);
                            hhdatentime.setText(date);

                            hhtitle = new TextView(HistoryTreasureActivity.this);
                            hhtitle.setPadding(50, 3, 50, 25);
                            hhtitle.setText(trans_types);

                            hhAmount = new TextView(HistoryTreasureActivity.this);
                            hhAmount.setPadding(20, 3, 30, 25);
                            hhAmount.setText(getTotal);


                            deleteR = new TextView(HistoryTreasureActivity.this);
                            deleteR.setPadding(100, 20, 0, 50);
                            deleteR.setText("Delete");

                            viewdetails = new TextView(HistoryTreasureActivity.this);
                            viewdetails.setPadding(100, 20, 0, 0);
                            viewdetails.setText("View");

                            // Create a horizontal line view
                            View horizontalLine = new View(HistoryTreasureActivity.this);
                            horizontalLine.setBackgroundColor(Color.BLACK);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    3 // Set the desired height of the horizontal line
                            );
                            params.setMargins(0, 0, 0, 8); // Add bottom margin to adjust spacing
                            horizontalLine.setLayoutParams(params);
                            //Get the ID
                            final int id = Integer.parseInt(getact);
                            final int HistoryID = Integer.parseInt(getTransID);

                            //delete this
                            deleteR.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    deleteRow(id);

                                    // Retrieve the parent TableRow and remove it from the TableLayout
                                    TableRow parentRow = (TableRow) view.getParent();
                                    ((TableLayout) parentRow.getParent()).removeView(parentRow);
                                }
                            });

                            viewdetails.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    showAlertDialog(HistoryID);
                                }
                            });


                            hdate.addView(hhdatentime);
                            // Add TextViews to the TableRow
                            headertitle.addView(hhtitle);
                            headertitle.addView(hhAmount);

                            haction.addView(deleteR);
                            haction.addView(viewdetails);

                            // Add the horizontal line view below the TableRow
                            historytrans.addView(horizontalLine);


// Add the TableRows to the TableLayout
                            historytrans.addView(hdate);
                            historytrans.addView(headertitle);
                            historytrans.addView(haction);

                        }
                    } else {
                        String message = obj.getString("message");
                        Toast.makeText(HistoryTreasureActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(HistoryTreasureActivity.this, "JSONException: " + e.getMessage() + s, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(HistoryTreasureActivity.this, "Exception: " + e.getMessage() + s, Toast.LENGTH_SHORT).show();
                }
            }

        }
        historyoftrans history = new historyoftrans();
        history.execute();
    }

    //delete row
    public void deleteRow(int Id) {
        class deletethis extends AsyncTask<Void, Void, String> {
            ProgressDialog pdLoading = new ProgressDialog(HistoryTreasureActivity.this);
            int id;
            public deletethis(int id) {
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
                params.put("transh_ID", String.valueOf(id));
                return requestHandler.sendPostRequest(historyfunction, params);
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
                            Toast.makeText(HistoryTreasureActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(HistoryTreasureActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(HistoryTreasureActivity.this, "Empty response received from the server", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(HistoryTreasureActivity.this, "JSONException: " + e.getMessage() + s, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                } catch (Exception e) {
                    Toast.makeText(HistoryTreasureActivity.this, "Exception: " + e.getMessage() + s, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }

        deletethis delete = new deletethis(Id);
        delete.execute();
    }


    //view details
    private void showAlertDialog(int hID) {
        Dialog dialog = new Dialog(HistoryTreasureActivity.this);
        dialog.setContentView(R.layout.reviewtransaction);

        TextView Dateuseracc = dialog.findViewById(R.id.Dateuseracc);
        TextView totaltxt = dialog.findViewById(R.id.totaltxt);
        TextView typetxt = dialog.findViewById(R.id.typetxt);
        Button exitbtn = dialog.findViewById(R.id.exitbtn); // Add a button in the layout if needed

        class TransactionView extends AsyncTask<Void, Void, String>{
            ProgressDialog pdLoading = new ProgressDialog(HistoryTreasureActivity.this);

            int hID;

            public TransactionView(int hID) {
                this.hID = hID;
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
                params.put("historyID", String.valueOf(hID));
                params.put("userID", uid);
                return requestHandler.sendPostRequest(historyfunction, params);
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pdLoading.dismiss();

                try {
                    JSONObject obj = new JSONObject(s);
                    if (!obj.getBoolean("error")) {
                        String date = obj.getString("Trans_Date");
                        String Total = obj.getString("Trans_Total");
                        String Status = obj.getString("Trans_Status");

                        Dateuseracc.setText("Date: " + date);
                        totaltxt.setText("Amount: " + Total);
                        typetxt.setText("Status: " + Status);

                    } else {
                        Toast.makeText(HistoryTreasureActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(HistoryTreasureActivity.this, "Exception: " + e + s, Toast.LENGTH_LONG).show();
                }
            }
        }
        TransactionView viewtransaction = new TransactionView(hID);
        viewtransaction.execute();


        exitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // Dismiss the dialog when the button is clicked
            }
        });

        dialog.show();
    }

}