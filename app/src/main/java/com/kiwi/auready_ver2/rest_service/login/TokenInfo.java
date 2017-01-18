package com.kiwi.auready_ver2.rest_service.login;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kiwi on 6/23/16.
 */
public class TokenInfo {

    @SerializedName("access_token")
    private String access_token;
    @SerializedName("token_type")
    private String token_type;

    public TokenInfo(String access_token, String token_type) {
        this.access_token = access_token;
        this.token_type = token_type;
    }

    public String getAccessToken() {
        return access_token;
    }

    public String getTokenType() {
        return token_type;
    }
}
