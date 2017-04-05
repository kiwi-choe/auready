package com.kiwi.auready_ver2.data;

import com.google.gson.annotations.SerializedName;

/**
 * basic user info
 */
public class User {
    @SerializedName("id")
    private String id;

    @SerializedName("email")
    private String email;

    @SerializedName("name")
    private String name;

    public User(String id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }
}
