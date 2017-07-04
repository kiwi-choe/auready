package com.kiwi.auready.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Should call this util, before using network
 */

public class NetworkUtils {

    public static boolean isOnline(@NonNull Context context) {
        checkNotNull(context);
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo active = connMgr.getActiveNetworkInfo();
        return (active != null && active.isConnected());
    }
}
