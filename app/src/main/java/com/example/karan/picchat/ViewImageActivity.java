package com.example.karan.picchat;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


public class ViewImageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        // Show the up button in the action bar
        setupActionBar();

        ImageView imageView = (ImageView)findViewById(R.id.imageView);
        Uri imageUri = getIntent().getData();
        /*
            setImageURI method works only for the images that are
            only on the device. but we need the images that are on our
            backend
         */
        //imageView.setImageURI();
        /*
            so we use library called "picasso". Its an open source library
         */
        Picasso.with(this).load(imageUri.toString()).into(imageView);
    }

    private void setupActionBar() {

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
