package com.kiwi.auready_ver2.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;

/**
 * Model Friend for remote and local
 */
public class Friend implements Parcelable {

    public static final String KEY = "friendList";
    @SerializedName("_id")
    private String id;         // column id of Friend table
    @SerializedName("email")
    private String email;
    @SerializedName("name")
    private String name;

    public Friend() {
        // default constructor
        super();
    }
    public String getEmail() {
        return this.email;
    }

    public String getName() {
        return this.name;
    }

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
