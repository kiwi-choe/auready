package com.kiwi.auready_ver2.rest_service;

import com.kiwi.auready_ver2.login.ClientCredentials;
import com.kiwi.auready_ver2.login.TokenInfo;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by kiwi on 6/14/16.
 */
public interface ILoginService {

    @POST("/oauth2/token")
    Call<TokenInfo> login(@Body ClientCredentials clientCredentials);
}
