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
    private String id;         // column id of Friend table

    private String email;
    private String name;

    public Friend() {
        // default constructor
        super();
    }
    /*
    * Use this constructor to create a new Friend.
    * */
    public Friend(String email, String name) {
        id = UUID.randomUUID().toString();
        this.email = email;
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public String getName() {
        return this.name;
    }

    /*
    * Use this constructor to create a friend if the Friend already has an id.
    * */
    public Friend(String id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Friend friend = (Friend)o;
        return Objects.equal(this.id, friend.id) &&
                Objects.equal(this.email, friend.getEmail());
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
        dest.writeString(id);
        dest.writeString(email);
        dest.writeString(name);
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
        id = in.readString();
        email = in.readString();
        name = in.readString();
    }
}
