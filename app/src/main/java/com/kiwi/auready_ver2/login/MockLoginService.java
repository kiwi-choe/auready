package com.kiwi.auready_ver2.login;

import com.kiwi.auready_ver2.data.api_model.ClientCredential;
import com.kiwi.auready_ver2.data.api_model.TokenInfo;
import com.kiwi.auready_ver2.rest_service.ILoginService;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.mock.BehaviorDelegate;

/**
 * Created by kiwi on 6/24/16.
 */
public class MockLoginService implements ILoginService {

    private final BehaviorDelegate<ILoginService> delegate;

    public MockLoginService(retrofit2.mock.BehaviorDelegate<ILoginService> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Call<TokenInfo> login(@Body ClientCredential clientCredential) {
        TokenInfo tokenInfo = new TokenInfo("access token1", "token type1");
        return delegate.returningResponse(tokenInfo).login(clientCredential);
    }
}
