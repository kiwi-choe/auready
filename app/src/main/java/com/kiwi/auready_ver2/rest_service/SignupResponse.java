package com.kiwi.auready_ver2.rest_service;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kiwi on 6/17/16.
 */
public class SignupResponse {

    @SerializedName("email")
    private String email;


    public SignupResponse(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
