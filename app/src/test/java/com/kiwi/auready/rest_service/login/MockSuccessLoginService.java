package com.kiwi.auready.rest_service.login;

import com.kiwi.auready.data.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Path;
import retrofit2.mock.BehaviorDelegate;

/**
 * Success Login Service Mock
 */
public class MockSuccessLoginService implements ILoginService {

    private static final String STUB_ACCESSTOKEN = "stub_accessToken";
    private static final String STUB_REFRESHTOKEN = "stub_refreshToken";
    static final String STUB_NAME = "loggedInName";
    private static final String STUB_EMAIL = "stub_email";

    private static final User STUB_USERINFO = new User("stub_id", STUB_EMAIL, STUB_NAME);
    public static final LoginResponse RESPONSE =
            new LoginResponse(STUB_ACCESSTOKEN, STUB_REFRESHTOKEN, STUB_USERINFO);

    private final BehaviorDelegate<ILoginService> delegate;

    public MockSuccessLoginService(retrofit2.mock.BehaviorDelegate<ILoginService> delegate) {
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
