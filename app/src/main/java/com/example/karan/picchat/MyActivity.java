package com.example.karan.picchat;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseAnalytics;
import com.parse.ParseUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MyActivity extends FragmentActivity implements ActionBar.TabListener
{
    public static final String TAG = MyActivity.class.getSimpleName();

    public static final int TAKE_PHOTO_REQUEST =0;
    public static final int TAKE_VIDEO_REQUEST =1;
    public static final int PICK_PHOTO_REQUEST =2;
    public static final int PICK_VIDEO_REQUEST =3;

    public static final int MEDIA_TYPE_IMAGE=4;
    public static final int MEDIA_TYPE_VIDEO=5;

    public static final int FILE_SIZE_LIMIT = 1024*1024*10; // = 10MB

    protected Uri mMediaUri;  // uniform resource identifier


    protected DialogInterface.OnClickListener mDialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            switch (which)
            {
                case 0:     // take picture
                    Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                    if(mMediaUri==null)
                    {
                        //display message
                        Toast.makeText(MyActivity.this,getString(R.string.
                                error_external_storage),Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT,mMediaUri);
                        // means the activity should exit in return of result back to us
                        startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);
                    }

                    break;
                case 1:     //take video
                    Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
                    if(mMediaUri==null)
                    {
                        //display message
                        Toast.makeText(MyActivity.this,getString(R.string.
                                error_external_storage),Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        videoIntent.putExtra(MediaStore.EXTRA_OUTPUT,mMediaUri);
                        videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 9); // video time upto 10 sec or 10 MB???
                        //videoIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT,9000);
                        videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,0);    // 0 = very low quality,  1 = HQ.
                        // means the activity should exit in return of result back to us
                        startActivityForResult(videoIntent, TAKE_VIDEO_REQUEST);
                    }

                    break;
                case 2:     // choose picture
                    Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    choosePhotoIntent.setType("image/*");
                    startActivityForResult(choosePhotoIntent,PICK_PHOTO_REQUEST);
                    break;
                case 3:     // choose video
                    Intent chooseVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    chooseVideoIntent.setType("video/*");
                    Toast.makeText(MyActivity.this, getString(R.string.video_file_size_warning),Toast.LENGTH_LONG).show();
                    startActivityForResult(chooseVideoIntent, PICK_VIDEO_REQUEST);
                    break;

            }
        }

        private Uri getOutputMediaFileUri(int mediaTypeImage) {
            // To be safe, you should check that the SDCard is mounted
            // using Environment.getExternalStorageState() before doing this.
            if(isExternalStorageAvailable()) {
                //get uri

                // 1. Get the external storage directory
                String appName = MyActivity.this.getString(R.string.app_name);
                File mediaStorageDir = new File(Environment.
                        getExternalStoragePublicDirectory(Environment.
                                DIRECTORY_PICTURES), appName);

                // 2. Create our sub directory
                if(! mediaStorageDir.exists()) {
                    if(! mediaStorageDir.mkdirs()){
                        Log.e(TAG, "Failed to create directory.");
                    }

                }
                //  3. Create a file name
                //  4. Create the file
                File mediaFile;
                Date now = new Date();
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.US).format(now);
                String path = mediaStorageDir.getPath()+File.separator;
                if(mediaTypeImage == MEDIA_TYPE_IMAGE){
                    mediaFile=new File(path + "IMG" + timestamp+ ".jpg");
                }
                else if(mediaTypeImage == MEDIA_TYPE_VIDEO)
                {
                    mediaFile=new File(path + "VID" + timestamp+ ".mp4");
                }
                else
                    return null;
                Log.d(TAG,"File: "+ Uri.fromFile(mediaFile));
                //   5. Return the file's URI
                return Uri.fromFile(mediaFile);
            }
            else
                return null;

        }
        private boolean isExternalStorageAvailable()
        {
            String state = Environment.getExternalStorageState();
            if(state.equals(Environment.MEDIA_MOUNTED))
                return true;
            else
                return false;
        }

    };



    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */

    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_my);

        ParseAnalytics.trackAppOpened(getIntent());
        ParseUser currentUser = ParseUser.getCurrentUser();
        if(currentUser == null)
        {
            navigateToLogin();
        }
        else
        {
            Log.i(TAG, currentUser.getUsername());
            //System.out.println("Current User is: "+currentUser.getUsername());
        }

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        //mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
        mSectionsPagerAdapter = new SectionsPagerAdapter(this,getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }



    // Now we want to store the image clicked in our gallery
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            // ADD it to gallery
            if(requestCode==PICK_PHOTO_REQUEST || requestCode==PICK_VIDEO_REQUEST)
            {
                if(data==null)
                {
                    Toast.makeText(this,getString(R.string.general_error),Toast.LENGTH_LONG).show();
                }
                else{
                    mMediaUri = data.getData();
                }
                Log.i(TAG, "Media URI: "+mMediaUri);
                if(requestCode==PICK_VIDEO_REQUEST){
                    //make sure the file is less than 10MB
                    int fileSize = 0;
                    InputStream inputStream=null;
                    try {
                        inputStream = getContentResolver().
                                openInputStream(mMediaUri);
                        fileSize = inputStream.available();
                    }
                    catch (FileNotFoundException e){
                        Toast.makeText(this, getString(R.string.error_opening_file) +
                                "file. ",Toast.LENGTH_LONG).show();
                        return;
                    }
                    catch (IOException e){
                        Toast.makeText(this, getString(R.string.error_opening_file),Toast.LENGTH_LONG).show();
                        return;
                    }
                    finally {
                        try{
                            inputStream.close();
                        }
                        catch (IOException e){
                            //intentionally blank
                        }
                    }
                    if(fileSize>=FILE_SIZE_LIMIT)
                    {
                        Toast.makeText(this,getString(R.string.error_file_size_too_large),Toast.LENGTH_LONG).show();
                        return;
                    }
                }

            }
            else {
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(mMediaUri);  // specify path to file
                sendBroadcast(mediaScanIntent);
            }
            Intent recipientsIntent = new Intent(this,RecipientsActivity.class);
            recipientsIntent.setData(mMediaUri);

            String fileType;
            if(requestCode==PICK_PHOTO_REQUEST || requestCode==TAKE_VIDEO_REQUEST){
                fileType = ParseConstants.TYPE_IMAGE;
            }
            else{
                fileType=ParseConstants.TYPE_VIDEO;
            }
            recipientsIntent.putExtra(ParseConstants.KEY_FILE_TYPE,fileType);
            startActivity(recipientsIntent);
        }
        else if(resultCode != RESULT_CANCELED){
            Toast.makeText(this, getString(R.string.general_error) ,Toast.LENGTH_LONG).show();

        }
    }



    private void navigateToLogin() {
        Intent intent = new Intent(this,LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int itemId = item.getItemId();

        switch(itemId)
        {
            case R.id.action_settings:
                break;
            case R.id.action_logout:
                ParseUser.logOut();
                navigateToLogin();
                break;
            case R.id.action_edit_friends:
                Intent intent = new Intent(this, EditFriendsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_camera:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setItems(R.array.camera_choices,mDialogListener);
                AlertDialog dialog=builder.create();
                dialog.show();
                break;
            case R.id.action_message:

                //*********************************************************
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Send Message.");
                alert.setMessage("Type your message below.");
                // Set an EditText view to get user input
                final EditText input = new EditText(this);
                alert.setView(input);
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = input.getText().toString();
                        Intent recipientsIntent = new Intent(MyActivity.this, RecipientsActivity.class);
                        recipientsIntent.putExtra(ParseConstants.KEY_MESSAGE, value);
                        recipientsIntent.putExtra(ParseConstants.KEY_FILE_TYPE, ParseConstants.TYPE_TEXT);
                        startActivity(recipientsIntent);
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });
                alert.show();
                //******************************************************
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }



    /**
     * A placeholder fragment containing a simple view.
     */
    /* public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
     /*   private static final String ARG_SECTION_NUMBER = "section_number";
    */
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
  /*      public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }
    } */

}