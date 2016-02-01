package com.ait.aitendance;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    @Bind(R.id.input_stuNo) EditText _stuNoText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.input_confirmPwd) EditText _confPwdText;
    @Bind(R.id.btn_signup) Button _signupButton;
    @Bind(R.id.link_login) TextView _loginLink;

    TextView inpStuNo;
    TextView inpPassword;
    TextView inpConfPwd;

    String studentNo;
    String password;
    String confPwd;
    String androidId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        studentNo = ((TextView) findViewById(R.id.input_stuNo)).getText().toString();
        password = ((TextView) findViewById(R.id.input_password)).getText().toString();
        confPwd = ((TextView)findViewById(R.id.input_confirmPwd)).getText().toString();

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = _stuNoText.getText().toString();
        String email = _passwordText.getText().toString();
        //String password = _confPwdText.getText().toString();




        // TODO: Implement your own signup logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);

        //Setting the Android ID||UUID
        androidId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

        //Connect to db
        new RegisterTask(this).execute();



        finish();
    }

    public void onSignupFailed() {
       Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        //String name = _stuNoText.getText().toString();
        //String email = _passwordText.getText().toString();
        //String password = _confPwdText.getText().toString();

       // Toast.makeText(this,studentNo,Toast.LENGTH_LONG).show();

        if (studentNo.isEmpty() || studentNo.length() != 4)
        {
            _stuNoText.setError("Student Number is a 4 digit number");
            valid = false;
        }
        else
        {
            _stuNoText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 && confPwd.isEmpty() || confPwd.length() < 4)
        {
            _passwordText.setError("longer than four");
            _confPwdText.setError("longer than four");
            valid = false;
        }
        else if(password.isEmpty() || password.length() < 4)
        {
            _passwordText.setError("longer than four");
            valid = false;
        }
        else if(confPwd.isEmpty() || confPwd.length() < 4)
        {
            _confPwdText.setError("longer than four");
            valid = false;
        }
        else if(!password.equals(confPwd))
        {
            _passwordText.setError("passwords don't match");
            _confPwdText.setError("passwords don't match");
            valid = false;
        }
        else
        {
            _confPwdText.setError(null);
            _passwordText.setError(null);
        }

        return valid;
    }

    //background task for registering a user
    public class RegisterTask extends AsyncTask<Void, Void, Void>
    {
        private boolean success = false;
        private String error = "";

        private Activity CurrentActivity;

        public RegisterTask(Activity CurrentActivity)
        {
            this.CurrentActivity = CurrentActivity;
        }

        @Override
        protected Void doInBackground(Void... params) {
            //Note, probably should pass these next 2 values in at constructor
            //too dependant on a particular layout
            EditText userNameET = (EditText) findViewById(R.id.input_stuNo);
            EditText passwordET = (EditText) findViewById(R.id.input_password);
            EditText confirmPasswordET = (EditText) findViewById(R.id.input_confirmPwd);

            JSONParser jsonParser = new JSONParser();

            String username = studentNo.concat("@ait.nsw.edu.au");

            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            parameters.add(new BasicNameValuePair("username",username));
            parameters.add(new BasicNameValuePair("password", password));
            parameters.add(new BasicNameValuePair("uuid",androidId));
            parameters.add(new BasicNameValuePair("studentNo",studentNo));
            JSONObject json = jsonParser.getJSONFromUrl("http://androidapi.mattconcepts.com/register.php", parameters);
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
                Toast.makeText(CurrentActivity, "Registration Successful!", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(CurrentActivity, "Failed: "+error, Toast.LENGTH_SHORT).show();
            }
        }
    }
}