package com.ait.aitendance;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Bind;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private final String Femail = "test@gmail.com";
    private final String Fpassword = "password";

    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.btn_login) Button _loginButton;
    @Bind(R.id.link_signup) TextView _signupLink;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");


        /* TURN THIS BACK ON BEFORE RELEASE TO VALIDATE FIELD */
        /*if (!validate()) {
            onLoginFailed();
            return;
        }*/

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.

        new android.os.Handler().postDelayed(
                new Runnable()
                {
                    public void run()
                    {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                //if(_passwordText.getText().toString() == Femail && _confPwdText.getText().toString() == Fpassword)
               // {
                    this.finish();
                //}
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    public class LoginTask extends AsyncTask<Void, Void, Void>
    {
        private boolean success = false;
        private String error = "";

        private Activity CurrentActivity;

        public LoginTask(Activity CurrentActivity)
        {
            this.CurrentActivity = CurrentActivity;
        }

        @Override
        protected Void doInBackground(Void... params) {
            //Note, probably should pass these next 2 values in at constructor
            //too dependant on a particular layout
            EditText userNameET = (EditText) findViewById(R.id.input_email);
            EditText passwordET = (EditText) findViewById(R.id.input_password);

            JSONParser jsonParser = new JSONParser();

            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            parameters.add(new BasicNameValuePair("username",userNameET.getText().toString()));
            parameters.add(new BasicNameValuePair("password", passwordET.getText().toString()));
            JSONObject json = jsonParser.getJSONFromUrl("http://androidapi.mattconcepts.com/login.php", parameters);
            if(json != null)
            {
                try{
                    if(json.getString("success")!=null)
                    {
                        String result = json.getString("success");
                        if(Integer.parseInt(result) == 1)
                        {
                            success = true;
                        }
                        else
                        {
                            success = false;
                            error = json.getString("error");
                        }
                    }
                }
                catch(JSONException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                success = false;
                error = "no response";
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //super.onPostExecute(aVoid);
            if(success)
            {
                //Toast.makeText(CurrentActivity, "Login Successful!", Toast.LENGTH_SHORT).show();

                //go to next screen
                //Intent nextScreen = new Intent(CurrentActivity, MainActivity2Activity.class);
                //startActivity(nextScreen);
            }
            else
            {
                Toast.makeText(CurrentActivity, "Failed: "+error, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
