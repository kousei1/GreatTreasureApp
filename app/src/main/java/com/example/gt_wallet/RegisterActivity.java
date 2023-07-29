package com.example.gt_wallet;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private Button cnclbtn, confirm;

    private EditText username, password, confirmpass, nameofuser,usernumber,useremail, bdayuser, useraddress, gender;

    private CheckBox male, female;
    public static final String register_user = "http://192.168.31.150/GreatTreasure/TreasureRegister.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        confirmpass = findViewById(R.id.confirmpass);
        nameofuser = findViewById(R.id.nameofuser);
        usernumber = findViewById(R.id.usernumber);
        useremail = findViewById(R.id.useremail);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        bdayuser = findViewById(R.id.bdayuser);
        useraddress = findViewById(R.id.useraddress);

        cnclbtn = findViewById(R.id.cnclbtn);
        confirm = findViewById(R.id.cnfm);

        cnclbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addStudent();
            }
        });

    }
    private void addStudent() {
        final String user = username.getText().toString();
        final String pass = password.getText().toString();
        final String conpass = confirmpass.getText().toString();
        final String name = nameofuser.getText().toString();
        final String number = usernumber.getText().toString();
        final String email = useremail.getText().toString();
        final String birth = bdayuser.getText().toString();
        final String address = useraddress.getText().toString();

        String errorMes = "";
        if (user.isEmpty() && pass.isEmpty() && conpass.isEmpty() && name.isEmpty() && number.isEmpty() && email.isEmpty() && birth.isEmpty() && address.isEmpty())
        {
            errorMes = "Please fill all the blank";
        }

        if(!errorMes.isEmpty())
        {
            Toast.makeText(getApplicationContext(), errorMes, Toast.LENGTH_LONG).show();
            return;
        }

        String gender = "";
        if (male.isChecked()) {
            gender = "male";
        } else if (female.isChecked()) {
            gender = "female";
        }

        String msgtoast = "";
        if (!pass.equals(conpass)) {
            msgtoast = "The password does not match";
        }
        if(!msgtoast.isEmpty())
        {
            Toast.makeText(getApplicationContext(), msgtoast, Toast.LENGTH_LONG).show();
            return;
        }

        String finalGender = gender;
        class AddStudentTask extends AsyncTask<Void, Void, String> {
            ProgressDialog pdLoading = new ProgressDialog(RegisterActivity.this);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                pdLoading.setMessage("\t loading...");
                pdLoading.setCancelable(false);
                pdLoading.show();
            }

            @Override
            protected String doInBackground(Void... voids) {

                // If all fields are valid, proceed with the request
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("username", user);
                params.put("password", pass);
                params.put("name", name);
                params.put("number", number);
                params.put("email", email);
                params.put("gender", finalGender);
                params.put("birthday", birth);
                params.put("address", address);

                return requestHandler.sendPostRequest(register_user, params);
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pdLoading.dismiss();

                try {
                    // Converting response to JSON object
                    JSONObject obj = new JSONObject(s);
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    } else {
                        Toast.makeText(getApplicationContext(),  obj.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(RegisterActivity.this, "Exception: " + e.getMessage() + s, Toast.LENGTH_LONG).show();
                }
            }
        }

        AddStudentTask addStudentTask = new AddStudentTask();
        addStudentTask.execute();
    }
}
