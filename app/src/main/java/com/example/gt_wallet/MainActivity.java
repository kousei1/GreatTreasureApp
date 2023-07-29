package com.example.gt_wallet;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private TextView signupbtn, forgotpass;

    private EditText username, password;
    private Button loginbtn;

    public static final String user_lgn = "http://192.168.31.150:8080/GreatTreasure/Treasurelogin.php";
    public static final String user_forgot = "http://192.168.31.150:8080/GreatTreasure/changepassword.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        signupbtn = findViewById(R.id.signupbtn);
        forgotpass = findViewById(R.id.forgotpass);
        loginbtn = findViewById(R.id.loginbtn);

        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showforgotpassform();
            }
        });

        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginvoid();
                username.setText("");
                password.setText("");
            }
        });
    }

    public void loginvoid() {
        final String user = username.getText().toString();
        final String pass = password.getText().toString();
            String msgtoast = "";
            if(user.isEmpty() && pass.isEmpty())
            {
                msgtoast = "Fill the blank";
            }else if (user.isEmpty())
            {
                msgtoast = "Please enter the username";
            }else if(pass.isEmpty())
            {
                msgtoast = "Please enter the password";
            }

            if (!msgtoast.isEmpty()) {
                Toast.makeText(MainActivity.this, msgtoast, Toast.LENGTH_SHORT).show();
                return;
            }

        class LoginUser extends AsyncTask<Void, Void, String> {

            ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);

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
                params.put("username", user);
                params.put("password", pass);

                return requestHandler.sendPostRequest(user_lgn, params);
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pdLoading.dismiss();

                try {
                    JSONObject obj = new JSONObject(s);
                    if (!obj.getBoolean("error")) {
                        // Login successful, extract user data
                        final String uid = obj.getString("uid");//get the values
                        final String nameofuser = obj.getString("name");
                        final String num = obj.getString("number");
                        final String position = obj.getString("position");

                        if (position.equalsIgnoreCase("user")) {
                            Toast.makeText(MainActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            intent.putExtra("uid", uid);//Stored uid
                            intent.putExtra("name", nameofuser);
                            intent.putExtra("number", num);
                            startActivity(intent);
                        }else {
                            startActivity(new Intent(MainActivity.this, TreasureIn.class));
                            Toast.makeText(MainActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(MainActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Exception: " + e + s, Toast.LENGTH_LONG).show();
                }
            }
        }
        LoginUser userlogin = new LoginUser();
        userlogin.execute();
    }

    public void showforgotpassform(){
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

// Set the custom layout
        dialog.setContentView(R.layout.activity_forgot_password);

// Set the dialog window width to match the parent
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(layoutParams);
        }

         EditText useremail, forgotpassword, retypepass;
         Button submit, cncl;

        useremail = dialog.findViewById(R.id.useremail);
        forgotpassword = dialog.findViewById(R.id.forgotpassword);
        retypepass = dialog.findViewById(R.id.retypepass);
        submit = dialog.findViewById(R.id.submit);
        cncl = dialog.findViewById(R.id.cncl);

        cncl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 final String email = useremail.getText().toString();
                 final String fpassword = forgotpassword.getText().toString();
                 final String retype = retypepass.getText().toString();
                String msg = "";
                if (!fpassword.equals(retype))
                {
                    msg = "Your password doesn't match";
                }

                if(email.isEmpty() && fpassword.isEmpty() && retype.isEmpty())
                {
                    msg = "Fill the blank";
                }

                if(!msg.isEmpty())
                {
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    return;
                }

                class changepassword extends AsyncTask<Void, Void, String>{
                    ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);

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
                        params.put("email", email);
                        params.put("fpassword", fpassword);

                        return requestHandler.sendPostRequest(user_forgot, params);
                    }


                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        pdLoading.dismiss();

                        try {
                            JSONObject obj = new JSONObject(s);
                            if (!obj.getBoolean("error")) {
                                // Login successful, extract user data
                                Toast.makeText(MainActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            } else {
                                Toast.makeText(MainActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "Exception: " + e + s, Toast.LENGTH_LONG).show();
                        }
                    }
                }
                changepassword changepass = new changepassword();
                changepass.execute();


            }
        });


        dialog.show();
    }






}