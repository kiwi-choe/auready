package com.kiwi.auready_ver2.friend;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.kiwi.auready_ver2.Injection;
import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.source.local.AccessTokenStore;
import com.kiwi.auready_ver2.util.ActivityUtils;

public class FindActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

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
}
