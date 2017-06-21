package com.kiwi.auready_ver2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.kiwi.auready_ver2.data.source.local.AccessTokenStore;
import com.kiwi.auready_ver2.login.LoginActivity;
import com.kiwi.auready_ver2.settings.SettingsFragment;
import com.kiwi.auready_ver2.taskheads.TaskHeadsActivity;
import com.kiwi.auready_ver2.util.LoginUtils;

public class IntroActivity extends AppCompatActivity {

    private static final int REQ_LOGIN = 1;
    public static final int REQ_TASKHEADSVIEW = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check isLoggedIn
        AccessTokenStore accessTokenStore = AccessTokenStore.getInstance(getApplicationContext());
        if (accessTokenStore.isLoggedIn()) {
            startTaskHeadsView();
        } else {
            startAccountView();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("Tag_TESTTT", "requestCode: " + String.valueOf(requestCode));
        if (requestCode == REQ_LOGIN && resultCode == RESULT_OK) {
            boolean isSuccess = data.getBooleanExtra(LoginUtils.IS_SUCCESS, false);
            if (isSuccess) {
                startTaskHeadsView();
            }
        } else if (requestCode == REQ_TASKHEADSVIEW && resultCode == RESULT_OK) {
            boolean isSuccess = data.getBooleanExtra(SettingsFragment.EXTRA_LOGOUT, false);
            if (isSuccess) {
                startAccountView();
            }
        } else {
            finish();
        }
    }

    private void startAccountView() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, REQ_LOGIN);
    }

    private void startTaskHeadsView() {
        Intent intent = new Intent(this, TaskHeadsActivity.class);
        startActivityForResult(intent, REQ_TASKHEADSVIEW);
    }
}
