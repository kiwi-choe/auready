package com.kiwi.auready_ver2.tasks;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.source.local.AccessTokenStore;
import com.kiwi.auready_ver2.friend.FriendActivity;
import com.kiwi.auready_ver2.login.LoginActivity;
import com.kiwi.auready_ver2.util.ActivityUtils;

import static com.google.common.base.Preconditions.checkNotNull;

public class TasksActivity extends AppCompatActivity
        implements TasksFragment.TasksFragmentListener {

    private static final String TAG = "Tag_MainActivity";

    private DrawerLayout mDrawerLayout;
    private TextView mNavHeaderName;
    private TextView mNavHeaderEmail;
    private Button mNavFriendButton;

    private TasksPresenter mPresenter;

    private AccessTokenStore mAccessTokenStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);


        TasksFragment tasksFragment =
                (TasksFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (tasksFragment == null) {
            tasksFragment = TasksFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), tasksFragment, R.id.content_frame, TasksFragment.TAG_TASKSFRAGMENT);
        }

        // Create the presenter
        mPresenter = new TasksPresenter(tasksFragment);
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

    }

    private void initView() {
        // Set toolbar
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

        // Set floating button
        FloatingActionButton fb = (FloatingActionButton) findViewById(R.id.fab_add_task);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        // Set Navigation view
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        checkNotNull(navigationView, "navigationView cannot be null");
        setupDrawerContent(navigationView);
        // Set Navigation header
        mNavHeaderName = (TextView) navigationView.findViewById(R.id.nav_name);
        mNavHeaderEmail = (TextView) navigationView.findViewById(R.id.nav_email);
        mNavFriendButton = (Button) navigationView.findViewById(R.id.bt_manage_friend);

        // if member or guest
        if (mAccessTokenStore.isLoggedIn()) {
            setMemberNavView();
        } else {
            setGuestNavView();
        }
        View navHeader = navigationView.getHeaderView(0);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.drawer_actions_guest, menu);
        return true;
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
                new Intent(TasksActivity.this, FriendActivity.class);
        startActivity(intent);
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, LoginActivity.REQ_LOGINOUT);
    }

    /*
    * TasksFragment listener
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
