package com.kiwi.auready.rest_service.login;

/**
 * Signup info
 */
public class SignupInfo {

    private String email;
    private String name;
    private String password;

    public SignupInfo() {
        // empty generator
    }

    public SignupInfo(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }
}
