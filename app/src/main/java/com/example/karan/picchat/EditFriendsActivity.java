package com.example.karan.picchat;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;


public class EditFriendsActivity extends ListActivity {
    public static final String TAG=EditFriendsActivity.class.getSimpleName();
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected List<ParseUser> mUsers;
    protected ParseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_edit_friends);

        //Show the up button in the action bar
        setupActionBar();

        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }




    private void setupActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    protected void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation= mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
        setProgressBarIndeterminateVisibility(true);

        ParseQuery<ParseUser> query = ParseUser.getQuery(); // means query is going to return list of ParseUser objects.
        query.orderByAscending(ParseConstants.KEY_USERNAME);
        // we limit by 1000. because returning a large no of users will take too long or will crash our app
        query.setLimit(1000);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                setProgressBarIndeterminateVisibility(false);
                if(e==null)
                {
                    // success
                    mUsers= users;
                    String[] usernames = new String[mUsers.size()];
                    int i = 0;
                    for(ParseUser user: mUsers)
                    {
                        usernames[i] = user.getUsername();
                        System.out.println("user["+i+"]: "+usernames[i]);
                        i++;
                        //create array adapter
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditFriendsActivity.this,
                                android.R.layout.simple_list_item_checked,usernames);
                        setListAdapter(adapter);

                        addFriendCheckmarks();
                    }
                }
                else
                {
                    System.out.println(" e is not null");
                    Log.e(TAG, e.getMessage());

                    AlertDialog.Builder builder= new AlertDialog.Builder(EditFriendsActivity.this);

                    builder.setMessage(e.getMessage());
                    builder.setTitle(getString(R.string.error_title));
                    builder.setPositiveButton(android.R.string.ok,null);
                    AlertDialog dialog=builder.create();
                    dialog.show();
                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_friends, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id)
        {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        /*if (id == R.id.action_settings) {
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    // when we click on the users to add them as friend
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if(getListView().isItemChecked(position))
        {
            //add friend
            mFriendsRelation.add(mUsers.get(position));
        }
        else{
            // remove friend
            mFriendsRelation.remove(mUsers.get(position));
        }
        mCurrentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e!=null){
                    Log.e(TAG,e.getMessage());
                }
            }
        });
    }
    private void addFriendCheckmarks()
    {
        mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                if(e==null)
                {
                    //list returned- look for a match
                    for(int i=0;i<mUsers.size();i++)
                    {
                        ParseUser user = mUsers.get(i);

                        for(ParseUser friend: friends)
                        {
                            if(friend.getObjectId().equals(user.getObjectId()))
                            {
                                getListView().setItemChecked(i,true);
                            }
                        }
                    }
                }
                else{
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }
}

