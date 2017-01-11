package com.kiwi.auready_ver2.rest_service;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.api_model.ClientCredential;
import com.kiwi.auready_ver2.data.api_model.LoginResponse;
import com.kiwi.auready_ver2.data.api_model.TokenInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.mock.BehaviorDelegate;

/**
 * Created by kiwi on 6/24/16.
 */
public class MockSuccessLoginService implements ILoginService {

    public static final String STUB_NAME = "loggedInName";
    private static final List<Friend> STUB_FRIENDS = Lists.newArrayList(new Friend("aa@aa.com", "aa"), new Friend("bb@bb.com", "bb"), new Friend("cc@cc.com", "cc"));
    public static final String STUB_FRIEND_ID = "friendIdOfUser";

    public static final LoginResponse RESPONSE =
            new LoginResponse(STUB_NAME, new TokenInfo("access token1", "token type1"), STUB_FRIEND_ID);

    private final BehaviorDelegate<ILoginService> delegate;

    public MockSuccessLoginService(retrofit2.mock.BehaviorDelegate<ILoginService> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Call<LoginResponse> login(@Body ClientCredential clientCredential) {

        return delegate.returningResponse(RESPONSE).login(clientCredential);
    }

    @Override
    public Call<Void> logout() {
        return null;
    }
}
