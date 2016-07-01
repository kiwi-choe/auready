package com.kiwi.auready_ver2.data;

import android.support.annotation.Nullable;

/**
 * Created by kiwi on 6/30/16.
 *
 * Immutable model class for a Friend
 */
public class Friend {

    private final String mEmail;

    public Friend(String email) {
        mEmail = email;
    }

    public String getEmail() {
        return mEmail;
    }
}
