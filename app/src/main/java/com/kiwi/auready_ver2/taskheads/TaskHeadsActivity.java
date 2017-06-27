package com.kiwi.auready_ver2.taskheads;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.kiwi.auready_ver2.Injection;
import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.source.local.AccessTokenStore;
import com.kiwi.auready_ver2.notification.NotificationPresenter;
import com.kiwi.auready_ver2.util.ActivityUtils;
import com.kiwi.auready_ver2.util.NetworkUtils;

import io.fabric.sdk.android.Fabric;

public class TaskHeadsActivity extends AppCompatActivity {

    public static final int REQ_ADD_TASKHEAD = 2;
    public static final int REQ_SETTINGS = 3;

    private static final String TAG = "Tag_TaskHeadsActivity";
    private static final int REQ_PLAY_SERVICES_RESOLUTION_REQUEST = 0;
    private static final int REQ_ACTION_WIRELESS_SETTINGS = 1;

    private TaskHeadsPresenter mPresenter;

    private CustomDialog mCustomDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_taskheads);

        TaskHeadsFragment taskHeadsFragment =
                (TaskHeadsFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (taskHeadsFragment == null) {
            taskHeadsFragment = TaskHeadsFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), taskHeadsFragment, R.id.content_frame, TaskHeadsFragment.TAG_TASKHEADSFRAGMENT);
        }

        // Create the presenter
        mPresenter = new TaskHeadsPresenter(
                Injection.provideUseCaseHandler(),
                taskHeadsFragment,
                Injection.provideGetTaskHeadDetails(getApplicationContext()),
                Injection.provideDeleteTaskHeads(getApplicationContext()),
                Injection.provideGetTaskHeadsCount(getApplicationContext()),
                Injection.provideUpdateTaskHeadsOrder(getApplicationContext()));

        // Create Singleton AccessTokenStore
        AccessTokenStore accessTokenStore = AccessTokenStore.getInstance(getApplicationContext());
        // Create the notification presenter for menu
        NotificationPresenter notificationPresenter = new NotificationPresenter(
                accessTokenStore.getStringValue(AccessTokenStore.ACCESS_TOKEN, null),
                Injection.provideUseCaseHandler(),
                taskHeadsFragment,
                Injection.provideGetNewNotificationsCount(getApplicationContext()));

        // Load previously saved state, if available.
        if (savedInstanceState != null) {
        }

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkNetworkConnection();
        checkGooglePlayService();

        supportInvalidateOptionsMenu();
    }

    private void checkNetworkConnection() {
        if(!NetworkUtils.isOnline(getApplicationContext())) {
            // Blocked View & Show the message
            mCustomDialog = new CustomDialog(this,
                    getString(R.string.connect_network_dialog),
                    mNetworkConnectionListener);
            mCustomDialog.show();
        } else {
            if(mCustomDialog != null && mCustomDialog.isShowing()) {
                mCustomDialog.dismiss();
            }
        }
    }

    private View.OnClickListener mNetworkConnectionListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            // Open SettingsActivity of Android
            startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), REQ_ACTION_WIRELESS_SETTINGS);
        }
    };

    private void checkGooglePlayService() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(this, status, REQ_PLAY_SERVICES_RESOLUTION_REQUEST).show();
                Log.d("Check_googleplay", "googlePlayService cannot be used");
            }
            // download google play service
        }
        Log.d("Check_googleplay", "googlePlayService can be used");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void initView() {

        String userName = AccessTokenStore.getInstance(getApplicationContext())
                .getStringValue(AccessTokenStore.USER_NAME, getString(R.string.app_name));

        // Set Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(false);
            ab.setTitle(userName);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.result(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.taskhead_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }
}
