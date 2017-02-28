package com.kiwi.auready_ver2.rest_service.login;

import com.google.gson.annotations.SerializedName;

/**
 * response of '/auth/token' api
 */
public class LoginResponse {

    // Token Info
    @SerializedName("access_token")
    private final String accessToken;
    @SerializedName("refresh_token")
    private final String refreshToken;

    // User Info
    @SerializedName("user_name")
    private final String userName;

    public LoginResponse(String accessToken, String refreshToken, String userName) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
