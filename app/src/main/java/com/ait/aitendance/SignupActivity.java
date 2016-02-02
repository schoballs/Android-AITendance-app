package com.ait.aitendance;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
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

        /*
        private TextView statusField,roleField;
        private Context context;
        private int byGetOrPost = 0;

        //flag 0 means get and 1 means post.(By default it is get.)
        public SigninActivity(Context context,TextView statusField,TextView roleField,int flag) {
            this.context = context;
            this.statusField = statusField;
            this.roleField = roleField;
            byGetOrPost = flag;
        }


        @Override
        protected String doInBackground(String... arg0) {
            if(byGetOrPost == 0){ //means by Get Method

                try{
                    String username = (String)arg0[0];
                    String password = (String)arg0[1];
                    String link = "http://myphpmysqlweb.hostei.com/login.php?username="+username+"& password="+password;

                    URL url = new URL(link);
                    HttpClient client = new DefaultHttpClient();
                    HttpGet request = new HttpGet();
                    request.setURI(new URI(link));
                    HttpResponse response = client.execute(request);
                    BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    in.close();
                    return sb.toString();
                }

                catch(Exception e){
                    return new String("Exception: " + e.getMessage());
                }
            }
            else{
                try{
                    String username = (String)arg0[0];
                    String password = (String)arg0[1];

                    String link="http://myphpmysqlweb.hostei.com/loginpost.php";
                    String data  = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
                    data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write( data );
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while((line = reader.readLine()) != null)
                    {
                        sb.append(line);
                        break;
                    }
                    return sb.toString();
                }
                catch(Exception e){
                    return new String("Exception: " + e.getMessage());
                }
            }
        }




        */
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
            JSONObject json = jsonParser.getJSONFromUrl("http://ait.interactivehippo.com.au/advancedstudio2/mobileapp/register.php", parameters);
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