package com.kiwi.auready_ver2.login;

import com.google.gson.Gson;
import com.kiwi.auready_ver2.rest_service.ErrorResponse;
import com.kiwi.auready_ver2.rest_service.ILoginService;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.mock.BehaviorDelegate;
import retrofit2.mock.Calls;

/**
 * Created by kiwi on 6/24/16.
 */
public class MockFailedLoginService {
    private final BehaviorDelegate<ILoginService> delegate;

    public MockFailedLoginService(BehaviorDelegate<ILoginService> delegate) {
        this.delegate = delegate;
    }

    public Call<TokenInfo> login(ClientCredentials clientCredentials) {

        ErrorResponse error = new ErrorResponse(404, "login failed");
        Gson gson = new Gson();
        String json = gson.toJson(error);
        Response response = Response.error(404, ResponseBody.create(MediaType.parse("application/json"), json));

        return delegate.returning(Calls.response(response)).login(clientCredentials);
    }
}
