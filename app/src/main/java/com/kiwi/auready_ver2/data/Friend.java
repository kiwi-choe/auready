package com.kiwi.auready_ver2.data;

import android.support.annotation.Nullable;

import com.google.common.base.Objects;

import java.util.UUID;

/**
 * Created by kiwi on 6/30/16.
 *
 * Immutable model class for a Friend
 */
public class Friend {

    private String mId;
    private final String mEmail;

    /*
    * Use this constructor to create a new Friend.
    * */
    public Friend(String email) {
        mId = UUID.randomUUID().toString();
        mEmail = email;
    }

    /*
    * Use this constructor to create a friend if the Friend already has an id.
    * */
    public Friend(String email, String id) {
        mId = id;
        mEmail = email;
    }

    public String getId() {
        return mId;
    }
    public String getEmail() {
        return mEmail;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Friend friend = (Friend)o;
        return Objects.equal(mId, friend.mId) &&
                Objects.equal(mEmail, friend.mEmail);
    }

    @Override
    public String toString() {
        return "Friend with Email " + mEmail;
    }
}
