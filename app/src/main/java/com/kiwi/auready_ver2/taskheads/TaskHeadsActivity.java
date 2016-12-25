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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kiwi.auready_ver2.Injection;
import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.source.local.AccessTokenStore;
import com.kiwi.auready_ver2.friend.FriendsActivity;
import com.kiwi.auready_ver2.login.LoginActivity;
import com.kiwi.auready_ver2.util.ActivityUtils;

import static com.google.common.base.Preconditions.checkNotNull;

public class TaskHeadsActivity extends AppCompatActivity
        implements TaskHeadsFragment.TaskHeadsFragmentListener {

    private static final String TAG = "Tag_MainActivity";

    public static final int REQ_LOGINOUT = 1;
    public static final int REQ_ADD_TASKHEAD = 2;

    private DrawerLayout mDrawerLayout;
    private TextView mNavHeaderName;
    private TextView mNavHeaderEmail;
    private Button mNavFriendButton;

    private TaskHeadsPresenter mPresenter;

    private AccessTokenStore mAccessTokenStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                Injection.provideGetTaskHeads(getApplicationContext()),
                Injection.provideDeleteTaskHead(getApplicationContext()),
                Injection.provideGetTaskHeadsCount(getApplicationContext()));

        // Load previously saved state, if available.
        if (savedInstanceState != null) {
        }


        // Create Singleton AccessTokenStore
        mAccessTokenStore = AccessTokenStore.getInstance(getApplicationContext());

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        supportInvalidateOptionsMenu();
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
        checkNotNull(navigationView, "navigationView cannot be null");
        setupDrawerContent(navigationView);
        // Set Navigation header
        View navHeader = navigationView.getHeaderView(0);
        mNavHeaderName = (TextView) navHeader.findViewById(R.id.nav_name);
        mNavHeaderEmail = (TextView) navHeader.findViewById(R.id.nav_email);
        mNavFriendButton = (Button) navHeader.findViewById(R.id.bt_manage_friend);

        // if member or guest
        if (mAccessTokenStore.isLoggedIn()) {
            setMemberNavView();
        } else {
            setGuestNavView();
        }
        setupNavHeaderContent(navHeader);
    }

    private void setupNavHeaderContent(View navHeader) {
        // clicked account settings button
        Button btAccountSettings = (Button) navHeader.findViewById(R.id.bt_account_settings);
        btAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoginActivity();
            }
        });

        // clicked friend button
        mNavFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFriendActivity();
            }
        });
    }

    private void setGuestNavView() {
        // Set Nav header
        mNavHeaderEmail.setVisibility(View.GONE);
        mNavHeaderName.setText(getString(R.string.nav_header_name_guest));

        mNavFriendButton.setVisibility(View.GONE);
    }

    private void setMemberNavView() {
        // Set Nav header
        mNavHeaderEmail.setVisibility(View.VISIBLE);
        mNavFriendButton.setVisibility(View.VISIBLE);

        Log.d("test!!", "entered setMemberNavView");

        mNavHeaderName.setText(mAccessTokenStore.getStringValue(AccessTokenStore.USER_NAME, "Not saved name"));
        mNavHeaderEmail.setText(mAccessTokenStore.getStringValue(AccessTokenStore.USER_EMAIL, "Not saved email"));
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.friend_navigation_menu_item:
                        startFriendActivity();
                        break;

                    case R.id.item_group:
                        startLoginActivity();
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

    private void startFriendActivity() {
        Intent intent =
                new Intent(TaskHeadsActivity.this, FriendsActivity.class);
        startActivity(intent);
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, REQ_LOGINOUT);
    }

    /*
    * TaskHeadsFragment listener
    * */
    @Override
    public void onLoginSuccess() {
        String loggedInName = mAccessTokenStore.getStringValue(AccessTokenStore.USER_NAME, "Not saved name");
        String loggedInEmail = mAccessTokenStore.getStringValue(AccessTokenStore.USER_EMAIL, "Not saved email");
        mNavHeaderName.setText(loggedInName);
        mNavHeaderEmail.setText(loggedInEmail);

        mDrawerLayout.openDrawer(GravityCompat.START);
    }
}
