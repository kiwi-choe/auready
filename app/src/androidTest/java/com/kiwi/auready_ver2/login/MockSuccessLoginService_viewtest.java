package com.kiwi.auready_ver2.login;

import com.kiwi.auready_ver2.rest_service.login.ClientCredential;
import com.kiwi.auready_ver2.rest_service.login.LoginResponse;
import com.kiwi.auready_ver2.rest_service.login.ILoginService;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Path;
import retrofit2.mock.BehaviorDelegate;

/**
 * Created by kiwi on 6/24/16.
 */
public class MockSuccessLoginService_viewtest implements ILoginService {


    public static final String STUB_ACCESSTOKEN = "stub_accessToken";
    public static final String STUB_REFRESHTOKEN = "stub_refreshToken";
    public static final String STUB_NAME = "loggedInName";

    public static final LoginResponse RESPONSE =
            new LoginResponse(STUB_ACCESSTOKEN, STUB_REFRESHTOKEN, STUB_NAME);

    private final BehaviorDelegate<ILoginService> delegate;

    public MockSuccessLoginService_viewtest(BehaviorDelegate<ILoginService> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Call<LoginResponse> login(@Body ClientCredential clientCredential) {

        return delegate.returningResponse(RESPONSE).login(clientCredential);
    }

    @Override
    public Call<Void> logout(@Path("token") String accessToken) {
        return null;
    }
}
