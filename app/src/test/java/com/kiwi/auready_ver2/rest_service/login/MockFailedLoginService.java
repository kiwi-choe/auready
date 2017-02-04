package com.kiwi.auready_ver2.rest_service.login;

import com.google.gson.Gson;
import com.kiwi.auready_ver2.rest_service.ErrorResponse;
import com.kiwi.auready_ver2.rest_service.login.ClientCredential;
import com.kiwi.auready_ver2.rest_service.login.LoginResponse;
import com.kiwi.auready_ver2.rest_service.login.ILoginService;

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

    public static final String ERROR_MESSAGE_400 = "login failed. Input the correct id or password";
    public static final String ERROR_MESSAGE_401 = "login failed.";

    private final BehaviorDelegate<ILoginService> delegate;

    public MockFailedLoginService(BehaviorDelegate<ILoginService> delegate) {
        this.delegate = delegate;
    }

    public Call<LoginResponse> failedLoginByInvalidUserInfo(ClientCredential clientCredential) {

        // R.string.login_fail_message_400
        ErrorResponse error = new ErrorResponse(400, ERROR_MESSAGE_400);
        Gson gson = new Gson();
        String json = gson.toJson(error);
        Response response = Response.error(400, ResponseBody.create(MediaType.parse("application/json"), json));

        return delegate.returning(Calls.response(response)).login(clientCredential);
    }

    public Call<LoginResponse> failedLoginByServerError(ClientCredential clientCredential) {

        // R.string.login_fail_message_401
        ErrorResponse error = new ErrorResponse(401, ERROR_MESSAGE_401);
        Gson gson = new Gson();
        String json = gson.toJson(error);
        Response response = Response.error(401, ResponseBody.create(MediaType.parse("application/json"), json));

        return delegate.returning(Calls.response(response)).login(clientCredential);
    }
}
