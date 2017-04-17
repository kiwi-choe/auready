package com.kiwi.auready_ver2.rest_service.task;

import com.google.gson.annotations.SerializedName;

/**
 * Model Member for remote
 */

public class Member_remote {
    @SerializedName("id")
    private final String id;
    @SerializedName("userId")
    private final String userId;
    @SerializedName("name")
    private final String name;
    @SerializedName("email")
    private final String email;

    public Member_remote(String id, String userId, String name, String email) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getUserId() {
        return userId;
    }
}
