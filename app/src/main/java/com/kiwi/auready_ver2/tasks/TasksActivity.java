package com.kiwi.auready_ver2.tasks;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.login.LoginActivity;
import com.kiwi.auready_ver2.util.ActivityUtils;

public class TasksActivity extends AppCompatActivity {

    private static final String TAG = "Tag_MainActivity";

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        // Set up the navigation drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawerLayout != null) {
            mDrawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
        }
        NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);

        // Account Layout
        if (navigationView != null) {
            View navHeader = navigationView.getHeaderView(0);
            RelativeLayout accountLayout = (RelativeLayout) navHeader.findViewById(R.id.nav_header_email);
            accountLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startLoginActivity();
                }
            });
        }

        TasksFragment tasksFragment =
                (TasksFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if(tasksFragment == null) {
            tasksFragment = TasksFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), tasksFragment, R.id.contentFrame);
        }
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
//        Intent intent = new Intent(TasksActivity.this, LoginActivity.class);
//        Intent intent = new Intent(this, LoginActivity.class);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
