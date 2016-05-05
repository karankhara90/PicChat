package com.example.karan.picchat;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class InboxActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        Log.e("TAG", "check 1 log");
        Toast.makeText(this,"check 1",Toast.LENGTH_LONG).show();


    }

    protected List<ParseObject> mMessages;

//    @Override
//    // NOTE: layouts and inflaters are used to create views
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // this line works like setContentView method works in Activity
//        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);
//        return rootView;
//    }

    @Override
    public void onResume() {
        super.onResume();
        this.setProgressBarIndeterminateVisibility(true);
        Context context = this;
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_MESSAGES);
        query.whereEqualTo(ParseConstants.KEY_RECIPIENT_IDS, ParseUser.getCurrentUser().getObjectId());
        query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e) {
//                this.setProgressBarIndeterminateVisibility(false);
                if (e == null) {
                    Toast.makeText(getApplicationContext(),"got messages",Toast.LENGTH_LONG).show();
                    // we found messages
                    mMessages = messages;

                    String[] usernames = new String[mMessages.size()];
                    int i = 0;
                    for (ParseObject message : mMessages) {
                        usernames[i] = message.getString(ParseConstants.KEY_SENDER_NAME);
                        System.out.println("user[" + i + "]: " + usernames[i]);
                        i++;
                    }


                    //create array adapter
                    // ArrayAdapter<String> adapter = new ArrayAdapter<String>(getListView().getContext(),
                    //      android.R.layout.simple_list_item_1, usernames);
                    //setListAdapter(adapter);
                    MessageAdapter adapter = new MessageAdapter(getListView().getContext(), mMessages);
                    setListAdapter(adapter);
                }
                else{
                    Toast.makeText(getApplicationContext(),"zero null messages",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        ParseObject message = mMessages.get(position);
        String messageType = message.getString(ParseConstants.KEY_FILE_TYPE);
        ParseFile file = message.getParseFile(ParseConstants.KEY_FILE);
        Uri fileUri = Uri.parse(file.getUrl());

        String senderName = message.getString(ParseConstants.KEY_SENDER_NAME);
        String displayMessage = message.getString(ParseConstants.KEY_MESSAGE);

        if(messageType.equals(ParseConstants.TYPE_IMAGE)){
            // view image
            Intent intent = new Intent(this, ViewImageActivity.class);
            intent.setData(fileUri);
            startActivity(intent);
        }
        else if(messageType.equals(ParseConstants.TYPE_VIDEO)){
            //view video
            Intent intent = new Intent(Intent.ACTION_VIEW, fileUri);
            intent.setDataAndType(fileUri,"video/*");
            startActivity(intent);
        }
        else if (messageType.equals(ParseConstants.TYPE_TEXT)){

            Toast.makeText(this, displayMessage, Toast.LENGTH_LONG).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Message from: " + senderName + ".");
            builder.setMessage(displayMessage);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }

}
