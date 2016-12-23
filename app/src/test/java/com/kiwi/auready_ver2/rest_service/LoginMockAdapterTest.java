package com.kiwi.auready_ver2.rest_service;

import com.kiwi.auready_ver2.data.api_model.ClientCredential;
import com.kiwi.auready_ver2.data.api_model.ErrorResponse;
import com.kiwi.auready_ver2.data.api_model.LoginResponse;
import com.kiwi.auready_ver2.login.IBaseUrl;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.mock.BehaviorDelegate;
import retrofit2.mock.MockRetrofit;
import retrofit2.mock.NetworkBehavior;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Login success&fail test
 */

public class LoginMockAdapterTest {

    private MockRetrofit mockRetrofit;
    private Retrofit retrofit;

    @Before
    public void setUp() throws Exception {
        retrofit = new Retrofit.Builder().baseUrl(IBaseUrl.BASE_URL)
                .client(new OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NetworkBehavior behavior = NetworkBehavior.create();

        mockRetrofit = new MockRetrofit.Builder(retrofit)
                .networkBehavior(behavior)
                .build();
    }

    @Test
    public void loginSuccess() throws IOException {

        BehaviorDelegate<ILoginService> delegate = mockRetrofit.create(ILoginService.class);
        ILoginService mockLoginService = new MockSuccessLoginService(delegate);

        // Create the loginInfo stub
        String email = "email";
        ClientCredential newCredentials = new ClientCredential(
                ClientCredential.CLIENT_ID,
                ClientCredential.GRANT_TYPE,
                email,
                "password");
        Call<LoginResponse> loginCall = mockLoginService.login(newCredentials);
        Response<LoginResponse> loginResponse = loginCall.execute();

        // Asserting response
        assertTrue(loginResponse.isSuccessful());
        assertEquals(loginResponse.body().getName(), MockSuccessLoginService.STUB_NAME);
    }

    @Test
    public void loginFailure_byInvalidUserInfo() throws IOException {

        BehaviorDelegate<ILoginService> delegate = mockRetrofit.create(ILoginService.class);
        MockFailedLoginService mockFailedLoginService = new MockFailedLoginService(delegate);

        // Create the invalid loginInfo stub
        String email = "unregistered-email@gmail.com";
        ClientCredential newCredentials = new ClientCredential(
                ClientCredential.CLIENT_ID,
                ClientCredential.GRANT_TYPE,
                email,
                "password");
        Call<LoginResponse> loginCall = mockFailedLoginService.failedLoginByInvalidUserInfo(newCredentials);
        Response<LoginResponse> loginResponse = loginCall.execute();

        Converter<ResponseBody, ErrorResponse> errConverter =
                retrofit.responseBodyConverter(ErrorResponse.class, new Annotation[0]);
        ErrorResponse error = errConverter.convert(loginResponse.errorBody());

        // Asserting response
        assertEquals(loginResponse.code(), 400);
        assertEquals(error.getMessage(), MockFailedLoginService.ERROR_MESSAGE_400);
    }

    @Test
    public void loginFailure_byServerError() throws IOException {

        BehaviorDelegate<ILoginService> delegate = mockRetrofit.create(ILoginService.class);
        MockFailedLoginService mockFailedLoginService = new MockFailedLoginService(delegate);

        // Create the loginInfo stub
        String email = "validEmail@gmail.com";
        ClientCredential newCredentials = new ClientCredential(
                ClientCredential.CLIENT_ID,
                ClientCredential.GRANT_TYPE,
                email,
                "password");
        Call<LoginResponse> loginCall = mockFailedLoginService.failedLoginByServerError(newCredentials);
        Response<LoginResponse> loginResponse = loginCall.execute();

        Converter<ResponseBody, ErrorResponse> errConverter =
                retrofit.responseBodyConverter(ErrorResponse.class, new Annotation[0]);
        ErrorResponse error = errConverter.convert(loginResponse.errorBody());

        // Asserting response
        assertEquals(loginResponse.code(), 401);
        assertEquals(error.getMessage(), MockFailedLoginService.ERROR_MESSAGE_401);
    }
}
