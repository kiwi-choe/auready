package com.kiwi.auready.rest_service.friend;

import com.google.gson.annotations.SerializedName;

/**
 * friend response
 */
public class FriendResponse {

    @SerializedName("id")
    private String id;
    @SerializedName("email")
    private String email;
    @SerializedName("name")
    private String name;

    public FriendResponse(String id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }


    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}
