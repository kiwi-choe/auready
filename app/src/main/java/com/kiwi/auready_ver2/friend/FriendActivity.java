package com.kiwi.auready_ver2.friend;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.FriendDataSource;
import com.kiwi.auready_ver2.data.FriendRepository;
import com.kiwi.auready_ver2.data.local.FriendLocalDataSource;
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
                    getSupportFragmentManager(), friendFragment, R.id.content_frame);
        }

        // Create Presenter
        mPresenter = new FriendPresenter(
                friendFragment,
                FriendRepository.getInstance(FriendLocalDataSource.getInstance(getApplicationContext()))
        );

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
