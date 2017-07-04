package com.kiwi.auready.rest_service.login;

import com.google.gson.annotations.SerializedName;
import com.kiwi.auready.data.User;

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
    @SerializedName("user_info")
    private User userInfo;

    public LoginResponse(String accessToken, String refreshToken, User userInfo) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userInfo = userInfo;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }


    public User getUserInfo() {
        return userInfo;
    }
}
