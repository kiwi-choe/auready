package com.kiwi.auready_ver2.rest_service;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.mock.BehaviorDelegate;

/**
 * Created by kiwi on 6/17/16.
 */
public class MockSignupService implements ISignupService {

    private final BehaviorDelegate<ISignupService> delegate;

    public MockSignupService(BehaviorDelegate<ISignupService> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Call<SignupResponse> signupLocal(@Body SignupInfo signupInfo) {
        SignupResponse response = new SignupResponse(SignupMockAdapterTest.STUB_EMAIL);
        return delegate.returningResponse(response).signupLocal(signupInfo);
    }
}
