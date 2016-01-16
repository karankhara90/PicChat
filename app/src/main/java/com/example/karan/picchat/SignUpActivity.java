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

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class SignUpActivity extends Activity {

    protected EditText mUsername;
    protected EditText mPassword;
    protected EditText mEmail;
    protected Button mSignUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this next line for progress bar
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_sign_up);

        mUsername = (EditText)findViewById(R.id.usernameField);
        mPassword = (EditText)findViewById(R.id.passwordField);
        mEmail = (EditText)findViewById(R.id.emailField);
        mSignUpButton = (Button)findViewById(R.id.signupButton);

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();
                String email = mEmail.getText().toString();
                // now we trim the whitespaces, in case user enters space accidently
                username=username.trim();
                password=password.trim();
                email=email.trim();

                if(username.isEmpty() || password.isEmpty() || email.isEmpty())
                {
                    AlertDialog.Builder builder= new AlertDialog.Builder((SignUpActivity.this));

                    builder.setMessage(getString(R.string.signup_error_message));
                    builder.setTitle(getString(R.string.signup_error_title));
                    builder.setPositiveButton(android.R.string.ok,null);
                    AlertDialog dialog=builder.create();
                    dialog.show();

                }
                else
                {
                    // create new user
                    setProgressBarIndeterminateVisibility(true);
                    ParseUser newUser = new ParseUser();
                    newUser.setUsername(username);
                    newUser.setPassword(password);
                    newUser.setEmail(email);

                    /* other fields can be set just like with ParseObject
                    newUser.put("phone", "650-555-0000");
                    newUser.put("Major","computer science"); */


                    /* signUpInBackground method does the similar thing, as AsyncTask does.
                       This saves the user in the background thread and calls a special callback method when its complete
                      */
                    newUser.signUpInBackground(new SignUpCallback()
                    {
                        public void done(ParseException e) {
                            setProgressBarIndeterminateVisibility(false);
                            if (e == null) {
                                // Hooray! Let them use the app now.
                                Intent intent = new Intent(SignUpActivity.this, MyActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); // also clear the old one
                                startActivity(intent);
                            }
                            else
                            {
                                // Sign up didn't succeed. Look at the ParseException to figure out what went wrong
                                AlertDialog.Builder builder= new AlertDialog.Builder(SignUpActivity.this);

                                builder.setMessage(e.getMessage());
                                builder.setTitle(getString(R.string.signup_error_title));
                                builder.setPositiveButton(android.R.string.ok,null);
                                AlertDialog dialog=builder.create();
                                dialog.show();
                            }
                        }
                    });
                }

            }
        });
    }


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
