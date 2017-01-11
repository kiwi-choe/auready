package com.kiwi.auready_ver2.data.api_model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kiwi on 8/2/16.
 */
public class LoginResponse {

    @SerializedName("name")
    private final String name;

    @SerializedName("tokenInfo")
    private final TokenInfo tokenInfo;

    @SerializedName("friendId")
    private final String friendId;

    public LoginResponse(String name, TokenInfo tokenInfo, String friendId) {
        this.name = name;
        this.tokenInfo = tokenInfo;
        this.friendId = friendId;
    }

    public TokenInfo getTokenInfo() {
        return tokenInfo;
    }

    public String getName() {
        return name;
    }

    public String getFriendId() {
        return friendId;
    }
}
