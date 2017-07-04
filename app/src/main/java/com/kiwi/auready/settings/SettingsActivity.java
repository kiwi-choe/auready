package com.kiwi.auready.settings;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.kiwi.auready.Injection;
import com.kiwi.auready.R;
import com.kiwi.auready.util.ActivityUtils;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Set up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if(ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(getString(R.string.settings_title));
        }
        SettingsFragment fragment =
                (SettingsFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if(fragment == null) {
            fragment = SettingsFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), fragment, R.id.content_frame, SettingsFragment.TAG);
        }

        SettingsPresenter presenter = new SettingsPresenter(
                Injection.provideUseCaseHandler(),
                fragment,
                Injection.provideInitializeLocalData(getApplicationContext()));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
