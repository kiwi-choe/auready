package com.kiwi.auready_ver2.rest_service;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Call;
import retrofit2.mock.BehaviorDelegate;
import retrofit2.mock.Calls;

/**
 * Created by kiwi on 6/18/16.
 */
public class MockFailedSignupService {

    private static final String TAG = "MockFailedSignup";
    private final BehaviorDelegate<ISignupService> delegate;

    public MockFailedSignupService(BehaviorDelegate<ISignupService> delegate) {
        this.delegate = delegate;
    }

    public Call<SignupResponse> signupLocal(SignupInfo signupInfo) {

        String email = signupInfo.getEmail();
        ErrorResponse error = new ErrorResponse(404, email + " is already registered");

        String json = "";
        Gson gson = new Gson();
        json = gson.toJson(error);
        Response response = Response.error(404, ResponseBody.create(MediaType.parse("application/json"), json));
        return delegate.returning(Calls.response(response)).signupLocal(signupInfo);
    }
}
