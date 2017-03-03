package com.kiwi.auready_ver2.rest_service.login;

import com.kiwi.auready_ver2.login.IBaseUrl;
import com.kiwi.auready_ver2.rest_service.ErrorResponse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.mock.BehaviorDelegate;
import retrofit2.mock.MockRetrofit;
import retrofit2.mock.NetworkBehavior;

/**
 * Signup service test
 */
public class SignupMockAdapterTest {

    public static final String STUB_EMAIL = "dd@gmail.com";
    public static final String STUB_NAME = "nameOfdd";

    private Retrofit mRetrofit;
    private MockRetrofit mockRetrofit;

    @Before
    public void setUp() throws Exception {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        Request request = original.newBuilder()
                                .header("Content-Type", "application/json")
                                .header("Accept", "application/json")
                                .method(original.method(), original.body())
                                .build();
                        Response response = chain.proceed(request);
                        return response;
                    }
                });
        OkHttpClient client = httpClient.build();
        mRetrofit = new Retrofit.Builder()
                .baseUrl(IBaseUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        NetworkBehavior behavior = NetworkBehavior.create();

        mockRetrofit = new MockRetrofit.Builder(mRetrofit)
                .networkBehavior(behavior)
                .build();
    }

    //Q mockService로 테스트를 하면, 실제 서버에 요청하여 받는 부분은 tdd로 어떻게 구현하지?
    @Test
    public void signupSuccess() throws Exception {
        BehaviorDelegate<ISignupService> delegate = mockRetrofit.create(ISignupService.class);
        ISignupService mockSignupService = new MockSuccessSignupService(delegate);

        // Create the signupInfo stub
        String email = STUB_EMAIL;
        String password = "123";
        String name = STUB_NAME;
        SignupInfo signupInfo = new SignupInfo(email, name, password);

        // Actual Test
        Call<SignupResponse> signupCall = mockSignupService.signupLocal(signupInfo);
        retrofit2.Response<SignupResponse> signupResponse = signupCall.execute();

        // Asserting response
        Assert.assertTrue(signupResponse.isSuccessful());
        Assert.assertEquals(email, signupResponse.body().getEmail());
    }

    @Test
    public void signupFailure() throws Exception {
        BehaviorDelegate<ISignupService> delegate = mockRetrofit.create(ISignupService.class);
        MockFailedSignupService mockSignupService = new MockFailedSignupService(delegate);

        // Create the signupInfo stub
        String email = "bbb@bbb.bbb";
        String name = "";
        String password = "123";
        SignupInfo signupInfo = new SignupInfo(email, name, password);

        Call<SignupResponse> signupCall = mockSignupService.signupLocal(signupInfo);
        retrofit2.Response<SignupResponse> signupResponse = signupCall.execute();
        Assert.assertFalse(signupResponse.isSuccessful());

        Converter<ResponseBody, ErrorResponse> errorConverter = mRetrofit.responseBodyConverter(ErrorResponse.class, new Annotation[0]);
        ErrorResponse error = errorConverter.convert(signupResponse.errorBody());

        // Asserting response
        Assert.assertEquals(404, signupResponse.code());
        Assert.assertEquals(email + " is already registered", error.getMessage());
    }
}
