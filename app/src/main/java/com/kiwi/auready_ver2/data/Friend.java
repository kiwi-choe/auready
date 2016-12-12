package com.kiwi.auready_ver2.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.base.Objects;

import java.util.UUID;

/**
 * Created by kiwi on 6/30/16.
 *
 * Immutable model class for a Friend
 * used by DB
 */
public class Friend implements Parcelable {

    public static final String KEY = "friendList";
    private String mId;         // column id of Friend table

    private String mEmail;
    private String mName;
    private String name;

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mEmail);
        dest.writeString(mName);
    }

    public static final Creator<Friend> CREATOR = new Creator<Friend>() {
        @Override
        public Friend createFromParcel(Parcel in) {
            return new Friend(in);
        }

        @Override
        public Friend[] newArray(int size) {
            return new Friend[size];
        }
    };

    protected Friend(Parcel in) {
        mId = in.readString();
        mEmail = in.readString();
        mName = in.readString();
    }

    // FIXME: 12/12/16 for test view
    public void setName(String name) {
        this.name = name;
    }
}
