package com.kiwi.auready_ver2.data;

import com.google.common.base.Objects;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by kiwi on 6/30/16.
 *
 * Immutable model class for a Friend
 * used by DB
 */
public class Friend {

    private String mId;         // column id of Friend table

    private String mEmail;
    private String mName;

    /*
    * Use this constructor to create a new Friend.
    * */
    public Friend(String email, String name) {
        mId = UUID.randomUUID().toString();
        mEmail = email;
        mName = name;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getName() {
        return mName;
    }

    /*
    * Use this constructor to create a friend if the Friend already has an id.
    * */
    public Friend(String id, String email, String name) {
        mId = id;
        mEmail = email;
        mName = name;
    }

    public String getId() {
        return mId;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Friend friend = (Friend)o;
        return Objects.equal(mId, friend.mId) &&
                Objects.equal(mEmail, friend.getEmail());
    }

    @Override
    public String toString() {
        return "Friend with Email " + getEmail();
    }
}
