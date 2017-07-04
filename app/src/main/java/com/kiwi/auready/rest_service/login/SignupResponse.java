package com.kiwi.auready.rest_service.login;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kiwi on 6/17/16.
 */
public class SignupResponse {

    // User info
    @SerializedName("email")
    private String email;
    @SerializedName("name")
    private String name;

    public  SignupResponse(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}
