package com.kiwi.auready_ver2.rest_service.login;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by kiwi on 6/24/16.
 */
public interface ISignupService {

    @POST("/local-account/signup")
    Call<SignupResponse> signupLocal(@Body SignupInfo signupInfo);
}
