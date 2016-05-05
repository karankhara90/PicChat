package com.example.karan.picchat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

//import com.facebook.CallbackManager;
//import com.facebook.FacebookCallback;
//import com.facebook.FacebookException;
//import com.facebook.FacebookSdk;
//import com.facebook.login.LoginResult;
//import com.facebook.login.widget.LoginButton;


public class LoginActivity extends Activity {

    protected EditText mUsername;
    protected EditText mPassword;
    protected Button mLoginButton;
    //private LoginButton mFbLoginButton;

    protected TextView mSignUpTextView;
   // private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this next line for progress bar
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_login);
        mSignUpTextView= (TextView)findViewById(R.id.signUpText);
        mSignUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        mUsername=(EditText)findViewById(R.id.usernameField);
        mPassword=(EditText)findViewById(R.id.passwordField);
        mLoginButton=(Button)findViewById(R.id.loginBtn);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();

                username=username.trim();
                password=password.trim();
                if(username.isEmpty() || password.isEmpty() )
                {
                    AlertDialog.Builder builder= new AlertDialog.Builder((LoginActivity.this));

                    builder.setMessage(getString(R.string.login_error_message));
                    builder.setTitle(getString(R.string.login_error_title));
                    builder.setPositiveButton(android.R.string.ok,null);
                    AlertDialog dialog=builder.create();
                    dialog.show();
                }
                else
                {
                    // Login
                    setProgressBarIndeterminateVisibility(true);
                    ParseUser.logInInBackground(username,password,new LogInCallback() {
                        @Override
                        public void done(ParseUser parseUser, ParseException e)
                        {
                            setProgressBarIndeterminateVisibility(false);
                            if(e==null)
                            {
                                //Success
                                Intent intent=new Intent(LoginActivity.this, MyActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                            else{
                                AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage(e.getMessage())
                                        .setTitle(R.string.login_error_title)
                                        .setPositiveButton(android.R.string.ok,null);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }
                    });

                }

//                mFbLoginButton = (LoginButton)findViewById(R.id.login_button);
//
//                // create a callback to handle the results of the login attempts and register it with the CallbackManager.
//                // Custom callbacks should implement FacebookCallback.
//                mFbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//                    @Override
//                    public void onSuccess(LoginResult loginResult) {   //If the login attempt is successful, onSuccess is called.
////                        info.setText(
////                                "User ID: "
////                                        + loginResult.getAccessToken().getUserId()
////                                        + "\n" +
////                                        "Auth Token: "
////                                        + loginResult.getAccessToken().getToken()
////                        );
//                        Toast.makeText(getApplicationContext(),"User ID: "+ loginResult.getAccessToken().getUserId()+ "\n" + "Auth Token: "
//                                        + loginResult.getAccessToken().getToken(), Toast.LENGTH_LONG).show();
//                    }
//
//                    @Override
//                    public void onCancel() {    //If the user cancels the login attempt, onCancel is called.
//                        Toast.makeText(getApplicationContext(),"FB Login attempt canceled.",Toast.LENGTH_LONG);
//                    }
//
//                    @Override
//                    public void onError(FacebookException e) {   //If an error occurs, onError is called.
//                        Toast.makeText(getApplicationContext(),"FB Login attempt failed.",Toast.LENGTH_LONG);
//                    }
//                });
            }
        });


        //The SDK needs to be initialized before using any of its methods
//        FacebookSdk.sdkInitialize(getApplicationContext());
//
//        //Next, initialize your instance of CallbackManager
//        callbackManager = CallbackManager.Factory.create();

    }
    //Tapping the login button (or fb login button) starts off a new Activity, which returns a result.
    // To receive and handle the result, override the onActivityResult method of your Activity and
    // pass its parameters to the onActivityResult method of CallbackManager.
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        callbackManager.onActivityResult(requestCode, resultCode, data);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
