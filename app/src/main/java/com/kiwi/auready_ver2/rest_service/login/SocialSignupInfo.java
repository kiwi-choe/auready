package com.kiwi.auready_ver2.rest_service.login;

import com.google.gson.annotations.SerializedName;

/**
 * signup info of Social account
 * 1. Google
 *
 */
public class SocialSignupInfo {

    public static final String GOOGLE = "google";

    @SerializedName("socialapp")
    private String socialApp;
    @SerializedName("id_token")
    private String idToken;

    public SocialSignupInfo(String socialApp, String idToken) {
        this.socialApp = socialApp;
        this.idToken = idToken;
    }

    public String getSocialApp() {
        return socialApp;
    }

    public String getIdToken() {
        return idToken;
    }
}
