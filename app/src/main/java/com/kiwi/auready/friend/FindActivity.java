package com.kiwi.auready.friend;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.kiwi.auready.Injection;
import com.kiwi.auready.R;
import com.kiwi.auready.data.source.local.AccessTokenStore;
import com.kiwi.auready.util.ActivityUtils;

public class FindActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

        // Set up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.find_title));
        }

        FindFragment findFragment =
                (FindFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if(findFragment == null) {
            findFragment = FindFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), findFragment, R.id.content_frame, FindFragment.TAG_FINDFRAG);
        }

        // Create Singleton AccessTokenStore
        AccessTokenStore accessTokenStore = AccessTokenStore.getInstance(getApplicationContext());
        String accessToken = accessTokenStore.getStringValue(AccessTokenStore.ACCESS_TOKEN, "");

        // Create Presenter
        FindPresenter presenter = new FindPresenter(
                accessToken,
                Injection.provideUseCaseHandler(),
                findFragment,
                Injection.provideSaveFriend(getApplicationContext()));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
