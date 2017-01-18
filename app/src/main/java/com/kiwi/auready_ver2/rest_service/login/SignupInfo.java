package com.kiwi.auready_ver2.rest_service.login;

/**
 * Signup info
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

    public String getEmail() {
        return email;
    }
}
