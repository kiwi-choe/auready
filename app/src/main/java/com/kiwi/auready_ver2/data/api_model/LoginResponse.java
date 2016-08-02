package com.kiwi.auready_ver2.data.api_model;

import com.google.gson.annotations.SerializedName;
import com.kiwi.auready_ver2.data.Friend;

import java.util.List;

/**
 * Created by kiwi on 8/2/16.
 */
public class LoginResponse {

    @SerializedName("tokenInfo")
    private TokenInfo tokenInfo;

    @SerializedName("friendList")
    private List<Friend> friends;

    public LoginResponse(TokenInfo tokenInfo, List<Friend> friends) {
        this.tokenInfo = tokenInfo;
        this.friends = friends;
    }

    public TokenInfo getTokenInfo() {
        return tokenInfo;
    }

    public List<Friend> getFriends() {
        return friends;
    }
}
