package com.kiwi.auready_ver2.rest_service.login;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;

/**
 * Created by kiwi on 6/14/16.
 */
public interface ILoginService {

    @POST("/auth/token")
    Call<LoginResponse> login(@Body ClientCredential clientCredential);

    @DELETE("/auth/token")
    Call<Void> logout();
}
