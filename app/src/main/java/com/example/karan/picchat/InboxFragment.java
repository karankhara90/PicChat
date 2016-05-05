package com.example.karan.picchat;

//import android.app.ListFragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class InboxFragment extends ListFragment {
    protected List<ParseObject> mMessages;

    @Override
    // NOTE: layouts and inflaters are used to create views
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // this line works like setContentView method works in Activity
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
//        getActivity().setProgressBarIndeterminateVisibility(true);
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_MESSAGES);
        query.whereEqualTo(ParseConstants.KEY_RECIPIENT_IDS, ParseUser.getCurrentUser().getObjectId());
        query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e) {
//                getActivity().setProgressBarIndeterminateVisibility(false);
                if (e == null) {
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
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        ParseObject message = mMessages.get(position);
        String messageType = message.getString(ParseConstants.KEY_FILE_TYPE);
        ParseFile file = message.getParseFile(ParseConstants.KEY_FILE);
        try {
            Uri fileUri = Uri.parse(file.getUrl());


            String senderName = message.getString(ParseConstants.KEY_SENDER_NAME);
            String displayMessage = message.getString(ParseConstants.KEY_MESSAGE);

            if (messageType.equals(ParseConstants.TYPE_IMAGE)) {
                // view image
                Intent intent = new Intent(getActivity(), ViewImageActivity.class);
                intent.setData(fileUri);
                startActivity(intent);
            } else if (messageType.equals(ParseConstants.TYPE_VIDEO)) {
                //view video
                Intent intent = new Intent(Intent.ACTION_VIEW, fileUri);
                intent.setDataAndType(fileUri, "video/*");
                startActivity(intent);
            } else if (messageType.equals(ParseConstants.TYPE_TEXT)) {

                try {
                    Toast.makeText(getActivity(), displayMessage, Toast.LENGTH_LONG).show();
                } catch (Exception ex) {
                    Log.e("TAG", "ex is =========================== " + ex);
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                try {
                    builder.setTitle("Message from: " + senderName + ".");
                } catch (Exception ex1) {
                    Log.e("TAG", "ex1 is =========================== " + ex1);
                }
                try {
                    builder.setMessage(displayMessage);
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    });
                } catch (Exception ex2) {
                    Log.e("TAG", "ex2 is =========================== " + ex2);
                }
                try {
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } catch (Exception ex3) {
                    Log.e("TAG", "ex3 is =========================== " + ex3);
                }

            }

        }catch (Exception exUri){
            Log.e("TAG", "exUri is =========================== " + exUri);
        }
    }
}