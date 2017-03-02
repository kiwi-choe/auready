package com.kiwi.auready_ver2.rest_service.login;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by kiwi on 6/14/16.
 */
public interface ILoginService {

    @POST("/auth/token")
    Call<LoginResponse> login(@Body ClientCredential clientCredential);

    @DELETE("/auth/token/{token}")
    Call<Void> logout(@Path("token") String accessToken);
}
