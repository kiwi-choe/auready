package com.kiwi.auready_ver2.notification;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.kiwi.auready_ver2.Injection;
import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.source.local.AccessTokenStore;
import com.kiwi.auready_ver2.util.ActivityUtils;

public class NotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        NotificationFragment notificationFragment =
                (NotificationFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if(notificationFragment == null) {
            notificationFragment = NotificationFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), notificationFragment, R.id.content_frame, NotificationFragment.TAG);
        }

        // Create Presenter
        Context context = getApplicationContext();
        String accessToken = AccessTokenStore.getInstance(context).getStringValue(AccessTokenStore.ACCESS_TOKEN, null);
        NotificationPresenter presenter = new NotificationPresenter(
                accessToken,
                Injection.provideUseCaseHandler(),
                notificationFragment,
                Injection.provideGetNotifications(context),
                Injection.provideReadNotification(context),
                Injection.provideDeleteNotification(context));
    }
}
