package com.kiwi.auready_ver2.friend;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.kiwi.auready_ver2.Injection;
import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.util.ActivityUtils;

import java.util.ArrayList;

public class FriendsActivity extends AppCompatActivity {

    public static final int REQ_FRIENDS = 1;
    public static final String ARG_SELECTED_FRIENDS = "arg_selected_friends";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        // Set up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowHomeEnabled(true);
            ab.setTitle(getApplicationContext().getResources().getString(R.string.friend_title));
        }

        FriendsFragment friendsFragment =
                (FriendsFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (friendsFragment == null) {
            friendsFragment = FriendsFragment.newInstance();

            Bundle bundle = new Bundle();
            if(getIntent().hasExtra(FriendsFragment.EXTRA_KEY_MEMBERS)) {
                ArrayList<String> friendIdOfMembers = getIntent().getStringArrayListExtra(FriendsFragment.EXTRA_KEY_MEMBERS);
                bundle.putStringArrayList(FriendsFragment.EXTRA_KEY_MEMBERS, friendIdOfMembers);
            }
            friendsFragment.setArguments(bundle);

            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), friendsFragment, R.id.content_frame, FriendsFragment.TAG_FRIENDFRAG);
        }

        // Create Presenter
        FriendsPresenter presenter = new FriendsPresenter(
                Injection.provideUseCaseHandler(),
                friendsFragment,
                Injection.provideGetFriends(getApplicationContext()),
                Injection.provideDeleteFriend(getApplicationContext()));

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.friend, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }
}
