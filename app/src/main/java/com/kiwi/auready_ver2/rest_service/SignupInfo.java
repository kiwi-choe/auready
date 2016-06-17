package com.kiwi.auready_ver2.rest_service;

/**
 * Created by kiwi on 6/14/16.
 */
public class SignupInfo {

    private String email;
    private String password;

    public SignupInfo() {
        // empty generator
    }

    public SignupInfo(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
