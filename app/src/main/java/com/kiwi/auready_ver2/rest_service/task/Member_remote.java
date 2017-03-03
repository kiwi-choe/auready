package com.kiwi.auready_ver2.rest_service.task;

import com.google.gson.annotations.SerializedName;

/**
 * Model Member for remote
 */

public class Member_remote {
    @SerializedName("id")
    private final String id;
    @SerializedName("name")
    private final String name;
    @SerializedName("email")
    private final String email;

    public Member_remote(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
