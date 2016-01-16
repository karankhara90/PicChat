package com.example.karan.picchat;


import android.app.Application;

import com.parse.Parse;
import com.parse.PushService;

public class PicChatApplication extends Application
{
    @Override
    public void onCreate() {
        super.onCreate();
        // here these 2 big codes in "" are: first one the application ID and cliend ID, needed to access backend.
        Parse.initialize(this, "cb3TJlTAd1U6OgSF6MNfSdphfZlmZ1yZAF9uUljm", "vHwa6Ti6VGQ46KuRZWJB0JbFC8tcQCSdl9OAxcJ4");
        PushService.setDefaultPushCallback(this, MyActivity.class);
               /* ParseObject testObject = new ParseObject("TestObject");
                testObject.put("foo","bar");
                testObject.saveInBackground(); */

                /*ParseUser user = new ParseUser();
                user.setUsername("my name");
                user.setPassword("my pass");
                user.setEmail("email@example.com");

                 // other fields can be set just like with ParseObject
                user.put("phone", "650-555-0000");

                user.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            // Hooray! Let them use the app now.
                        } else {
                            // Sign up didn't succeed. Look at the ParseException
                            // to figure out what went wrong
                        }
                    }

                    @Override
                    public void done(com.parse.ParseException e) {

                    }
                });  */




    }
}
