package com.example.karan.picchat;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;


public class RecipientsActivity extends ListActivity {

    public static final String TAG = RecipientsActivity.class.getSimpleName();
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected List<ParseUser> mFriends;
    protected ParseUser mCurrentUser;

    protected MenuItem mSendMenuItem;
    protected Uri mMediaUrl;
    protected String mFileType;
    protected String mMyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_recipients);
        // show the up button in the action bar/
        setupActionBar();
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mMediaUrl = getIntent().getData();
        mFileType = getIntent().getExtras().getString(ParseConstants.KEY_FILE_TYPE);
        //***************
        mMyMessage = getIntent().getExtras().getString(ParseConstants.KEY_MESSAGE);
        //***************
    }

    private void setupActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public void onResume()
    {
        super.onResume();
        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        setProgressBarIndeterminateVisibility(true);
        ParseQuery<ParseUser> query= mFriendsRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                setProgressBarIndeterminateVisibility(false);
                if (e == null) {
                    mFriends = friends;
                    String[] usernames = new String[mFriends.size()];
                    int i = 0;
                    for (ParseUser user : mFriends) {
                        usernames[i] = user.getUsername();
                        System.out.println("user[" + i + "]: " + usernames[i]);
                        i++;
                    }
                    //create array adapter
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getListView().getContext(),
                            android.R.layout.simple_list_item_checked, usernames);
                    setListAdapter(adapter);

                } else {
                    Log.e(TAG, e.getMessage());

                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);

                    builder.setMessage(e.getMessage());
                    builder.setTitle(getString(R.string.error_title));
                    builder.setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }


            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.recipients, menu);
        mSendMenuItem = menu.getItem(0); // 0 because position of just one item
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
            case android.R.id.home:
            /* This ID represents the Home or Up button.
              In case of this activity, the Up button is shown.
              Use NavUtils to allow users to navigate up one level in
              the application structure.
                */
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_send:

                    ParseObject message = createMessage();
                    if(message==null){
                        //error
                        try{
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setMessage(getString(R.string.error_selecting_file))
                                    .setTitle(getString(R.string.error_selecting_file_title))
                                    .setPositiveButton(android.R.string.ok,null);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                        catch (Exception excptn){
                            Log.e("TAG","excptn is in ---------------: "+excptn);
                        }

                    }
                    else{
                        try {
                            send(message);
                            finish();
                        }
                        catch (Exception excp){
                            Log.e("TAG","excp is in !!!!!!!!!!!!!!!!!!!!!!!!!!: "+excp);
                        }

                    }
                    return true;

        }
        return super.onOptionsItemSelected(item);
    }
    protected ParseObject createMessage() {
        ParseObject message = new ParseObject(ParseConstants.CLASS_MESSAGES);
        message.put(ParseConstants.KEY_SENDER_ID, ParseUser.getCurrentUser().getObjectId());
        message.put(ParseConstants.KEY_SENDER_NAME, ParseUser.getCurrentUser().getUsername());
        message.put(ParseConstants.KEY_RECIPIENT_IDS, getRecipientIds());

        message.put(ParseConstants.KEY_FILE_TYPE, mFileType);

        //*************
        if (mFileType.equals(ParseConstants.TYPE_TEXT)) {
            message.put("themessage", mMyMessage);
            message.put(ParseConstants.KEY_FILE_TYPE, "message");
            //return message;
        } else {
            //***************
            byte[] fileBytes = FileHelper.getByteArrayFromFile(this, mMediaUrl);
            if (fileBytes == null) {
                Toast.makeText(this, "mMedia is null",Toast.LENGTH_LONG);
                return null;
            } else {
                if (mFileType.equals(ParseConstants.TYPE_IMAGE)) {
                    fileBytes = FileHelper.reduceImageForUpload(fileBytes);
                }
                try{
                    String fileName = FileHelper.getFileName(this, mMediaUrl, mFileType);
                    ParseFile file = new ParseFile(fileName, fileBytes);
                    message.put(ParseConstants.KEY_FILE, file);
                }
                catch (Exception except){
                    Log.e("TAG","except is =========================== "+except);
                }

             }
            //return message;

        }
        return message;
    }
    protected ArrayList<String> getRecipientIds() {
        ArrayList<String> recipientIds = new ArrayList<String>();
        for(int i=0; i< getListView().getCount();i++)
        {
            if(getListView().isItemChecked(i)){
                recipientIds.add(mFriends.get(i).getObjectId());
            }
        }
        return recipientIds;
    }

    protected void send(ParseObject message){
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null){
                    //success
                    Toast.makeText(RecipientsActivity.this,getString(R.string.success_message),Toast.LENGTH_LONG).show();
                }
                else{
                    //error
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                    builder.setMessage(getString(R.string.error_sending_message))
                            .setTitle(getString(R.string.error_selecting_file_title))
                            .setPositiveButton(android.R.string.ok,null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if(l.getCheckedItemCount()>0){
            mSendMenuItem.setVisible(true);
        }
        else
            mSendMenuItem.setVisible(false);
    }

}
