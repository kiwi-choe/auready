package com.kiwi.auready_ver2.login;

import com.google.gson.Gson;
import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.api_model.ClientCredential;
import com.kiwi.auready_ver2.data.api_model.ErrorResponse;
import com.kiwi.auready_ver2.data.api_model.LoginResponse;
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

    public Call<LoginResponse> failedLoginByInvalidUserInfo(ClientCredential clientCredential) {

        // R.string.login_fail_message_400
        ErrorResponse error = new ErrorResponse(400, "login failed. Input the correct id or password");
        Gson gson = new Gson();
        String json = gson.toJson(error);
        Response response = Response.error(400, ResponseBody.create(MediaType.parse("application/json"), json));

        return delegate.returning(Calls.response(response)).login(clientCredential);
    }

    public Call<LoginResponse> failedLoginByServerError(ClientCredential clientCredential) {

        // R.string.login_fail_message_401
        ErrorResponse error = new ErrorResponse(401, "login failed.");
        Gson gson = new Gson();
        String json = gson.toJson(error);
        Response response = Response.error(401, ResponseBody.create(MediaType.parse("application/json"), json));

        return delegate.returning(Calls.response(response)).login(clientCredential);
    }
}
