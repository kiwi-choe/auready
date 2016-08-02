package com.kiwi.auready_ver2.login;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.api_model.ClientCredential;
import com.kiwi.auready_ver2.data.api_model.LoginResponse;
import com.kiwi.auready_ver2.data.api_model.TokenInfo;
import com.kiwi.auready_ver2.rest_service.ILoginService;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.mock.BehaviorDelegate;

/**
 * Created by kiwi on 6/24/16.
 */
public class MockLoginService implements ILoginService {

    private static List<Friend> STUB_FRIENDS = Lists.newArrayList(new Friend("aa@aa.com", "aa"), new Friend("bb@bb.com", "bb"), new Friend("cc@cc.com", "cc"));

    private final BehaviorDelegate<ILoginService> delegate;

    public MockLoginService(retrofit2.mock.BehaviorDelegate<ILoginService> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Call<LoginResponse> login(@Body ClientCredential clientCredential) {

        // TokenInfo Stub
        TokenInfo tokenInfo = new TokenInfo("access token1", "token type1");
        LoginResponse loginResponse = new LoginResponse(tokenInfo, STUB_FRIENDS);
        return delegate.returningResponse(loginResponse).login(clientCredential);
    }
}
