package com.kiwi.auready_ver2.rest_service.login;

/**
 * Created by kiwi on 6/23/16.
 *
 * Immutable model class for a ClientCredential
 */
public class ClientCredential {

    // Client ID for local
    public static final String CLIENT_ID = "tEYQAFiAAmLrS2Dl";
    public static final String GRANT_TYPE = "password";
    public static final String CLIENT_SECRET = "YmE2LYTGKunlVW5OrKNlWF9KQZUZOHDy";

    // info to get access token
    private String client_id;   // LOCAL_CLIENT_ID
    private String client_secret;   // LOCAL_CLIENT_SECRET
    private String grant_type;  // GRANT_TYPE_KEY
    private String username;    // USERNAME
    private String password;    // PASSWORD

    public ClientCredential(String client_id, String client_secret, String grant_type, String username, String password) {

        this.client_id = client_id;
        this.client_secret = client_secret;
        this.grant_type = grant_type;
        this.username = username;
        this.password = password;
    }

}
