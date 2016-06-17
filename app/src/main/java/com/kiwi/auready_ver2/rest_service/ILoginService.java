package com.kiwi.auready_ver2.rest_service;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by kiwi on 6/14/16.
 */
public interface ILoginService {

    @POST("/signup/local")
    Call<Void> requestSignupLocal(@Body SignupInfo signupInfo);
}
