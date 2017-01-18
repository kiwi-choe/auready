package com.kiwi.auready_ver2.rest_service.login;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.mock.BehaviorDelegate;

/**
 * Created by kiwi on 6/17/16.
 */
public class MockSuccessSignupService implements ISignupService {

    private final BehaviorDelegate<ISignupService> delegate;

    public MockSuccessSignupService(BehaviorDelegate<ISignupService> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Call<SignupResponse> signupLocal(@Body SignupInfo signupInfo) {
        SignupResponse response = new SignupResponse(SignupMockAdapterTest.STUB_EMAIL, SignupMockAdapterTest.STUB_NAME);
        return delegate.returningResponse(response).signupLocal(signupInfo);
    }
}
