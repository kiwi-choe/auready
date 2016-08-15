package com.kiwi.auready_ver2.tasks;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
    private NavigationView mNavigationView;
    private View mNavHeader;
    private TextView mNavHeaderName;
    private TextView mNavHeaderEmail;

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

        initComponents();
    }

    private void initComponents() {
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

        // Set Navigation view
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        checkNotNull(mNavigationView, "navigationView cannot be null");
        setupDrawerContent(mNavigationView);
        // if member or guest
        if (mAccessTokenStore.isLoggedIn()) {
            setMemberNavView();
        } else {
            setGuestNavView();
        }
        // Set Account layout on Nav header
        RelativeLayout accountLayout = (RelativeLayout) mNavHeader.findViewById(R.id.nav_header_account_layout);
        accountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoginActivity();
            }
        });
    }

    private void setGuestNavView() {

        // Set Navigation header
        mNavHeader = mNavigationView.inflateHeaderView(R.layout.nav_header_guest);
    }

    private void setMemberNavView() {

        // Set Navigation header
        mNavHeader = mNavigationView.inflateHeaderView(R.layout.nav_header);
        mNavHeaderName = (TextView) mNavHeader.findViewById(R.id.nav_name);
        mNavHeaderEmail = (TextView) mNavHeader.findViewById(R.id.nav_email);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.friend_navigation_menu_item:
                        Intent intent =
                                new Intent(TasksActivity.this, FriendActivity.class);
                        startActivity(intent);
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
