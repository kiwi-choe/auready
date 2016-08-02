package com.kiwi.auready_ver2.friend;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.kiwi.auready_ver2.Injection;
import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.util.ActivityUtils;

public class FriendActivity extends AppCompatActivity {

    private FriendPresenter mPresenter;

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
        }

        FriendFragment friendFragment =
                (FriendFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (friendFragment == null) {
            friendFragment = FriendFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), friendFragment, R.id.content_frame, FriendFragment.TAG_FRIENDFRAGMENT);
        }

        // Create Presenter
        mPresenter = new FriendPresenter(
                Injection.provideUseCaseHandler(),
                friendFragment,
                Injection.provideGetFriend(getApplicationContext())
        );

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
