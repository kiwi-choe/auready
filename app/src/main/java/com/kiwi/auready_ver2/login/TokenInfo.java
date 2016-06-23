package com.kiwi.auready_ver2.login;

/**
 * Created by kiwi on 6/23/16.
 */
public class TokenInfo {
    private String access_token;
    private String token_type;

    public TokenInfo(String access_token, String token_type) {
        this.access_token = access_token;
        this.token_type = token_type;
    }
}
