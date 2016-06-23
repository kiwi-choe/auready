package com.kiwi.auready_ver2.login;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.util.ActivityUtils;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.login);

        LoginFragment loginFragment = (LoginFragment)getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);
        if(loginFragment == null) {
            loginFragment = LoginFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    loginFragment, R.id.contentFrame);
        }
    }


}