package com.kiwi.auready_ver2;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.kiwi.auready_ver2.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private ViewGroup mLayoutNavHeader;
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
        mDrawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
        NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);

        mLayoutNavHeader = (ViewGroup)findViewById(R.id.layout_nav_header);
        if (mLayoutNavHeader != null) {
            mLayoutNavHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startLoginActivity();
                }
            });
        } else {
            Log.d("MAIN", "layout_nav_header is null");
        }
    }

    private void startLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
