package com.kiwi.auready_ver2.taskheads;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.kiwi.auready_ver2.Injection;
import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.source.local.AccessTokenStore;
import com.kiwi.auready_ver2.login.LoginActivity;
import com.kiwi.auready_ver2.notification.NotificationPresenter;
import com.kiwi.auready_ver2.util.ActivityUtils;

import io.fabric.sdk.android.Fabric;

import static com.google.common.base.Preconditions.checkNotNull;

public class TaskHeadsActivity extends AppCompatActivity
        implements TaskHeadsFragment.TaskHeadsFragmentListener {

    public static final int REQ_LOGIN = 1;
    public static final int REQ_ADD_TASKHEAD = 2;
    private static final String TAG = "Tag_TaskHeadsActivity";
    private static final int REQ_PLAY_SERVICES_RESOLUTION_REQUEST = 0;

    private DrawerLayout mDrawerLayout;
    private View mMemberNavHeader;
    private View mGuestNavHeader;

    private TaskHeadsPresenter mPresenter;

    private AccessTokenStore mAccessTokenStore;

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

        // Create Singleton AccessTokenStore
        mAccessTokenStore = AccessTokenStore.getInstance(getApplicationContext());

        // Create the presenter
        mPresenter = new TaskHeadsPresenter(
                Injection.provideUseCaseHandler(),
                taskHeadsFragment,
                Injection.provideGetTaskHeadDetails(getApplicationContext()),
                Injection.provideDeleteTaskHeads(getApplicationContext()),
                Injection.provideGetTaskHeadsCount(getApplicationContext()),
                Injection.provideUpdateTaskHeadsOrder(getApplicationContext()),
                Injection.provideInitializeLocalData(getApplicationContext()));


        // Create the notification presenter for menu
        NotificationPresenter notificationPresenter = new NotificationPresenter(
                mAccessTokenStore.getStringValue(AccessTokenStore.ACCESS_TOKEN, null),
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

        checkGooglePlayService();

        supportInvalidateOptionsMenu();
    }

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
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(Gravity.START);
            return;
        }

        super.onBackPressed();
    }

    private void initView() {

        // Set Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setHomeAsUpIndicator(R.drawable.ic_menu);
            ab.setDisplayHomeAsUpEnabled(true);
        }

        // Set Drawer layout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);

        // Set Navigation view
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        checkNotNull(navigationView, "mNavigationView cannot be null");
        setupDrawerContent(navigationView);
        mGuestNavHeader = navigationView.inflateHeaderView(R.layout.nav_header_guest);
        mMemberNavHeader = navigationView.inflateHeaderView(R.layout.nav_header);

        // Set Navigation header as login status
        if (mAccessTokenStore.isLoggedIn()) {
            setMemberNavView();
        } else {
            setGuestNavView();
        }
    }

    private void setGuestNavView() {

        mMemberNavHeader.setVisibility(View.GONE);
        mGuestNavHeader.setVisibility(View.VISIBLE);

        Button accountSettingsButton = (Button) mGuestNavHeader.findViewById(R.id.bt_account_settings);
        accountSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoginView();
            }
        });
    }

    private void setMemberNavView() {

        mGuestNavHeader.setVisibility(View.GONE);
        mMemberNavHeader.setVisibility(View.VISIBLE);

        TextView name = (TextView) mMemberNavHeader.findViewById(R.id.nav_name);
        TextView email = (TextView) mMemberNavHeader.findViewById(R.id.nav_email);

        String loggedInName = mAccessTokenStore.getStringValue(AccessTokenStore.USER_NAME, "Not saved name");
        String loggedInEmail = mAccessTokenStore.getStringValue(AccessTokenStore.USER_EMAIL, "Not saved email");
        name.setText(loggedInName);
        email.setText(loggedInEmail);

        Button logoutButton = (Button) mMemberNavHeader.findViewById(R.id.bt_logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.logout(mAccessTokenStore.getStringValue(AccessTokenStore.ACCESS_TOKEN, ""));
            }
        });
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.item_group:
                        startLoginView();
                        break;

                    default:
                        break;
                }

                // Close the navigation drawer when an item is selected.
                item.setChecked(true);
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Open the navigation drawer when the home icon is selected from the toolbar.
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startLoginView() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, REQ_LOGIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.result(requestCode, resultCode, data);
    }

    /*
        * TaskHeadsFragment listener
        * */
    @Override
    public void onUpdatingNavHeaderUI(boolean isLogin) {
        if (isLogin) {
            setMemberNavView();
        } else {
            setGuestNavView();
        }

        mDrawerLayout.openDrawer(GravityCompat.START);
    }
}
