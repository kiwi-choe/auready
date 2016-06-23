package com.kiwi.auready_ver2.login;

import com.kiwi.auready_ver2.rest_service.ILoginService;
import com.kiwi.auready_ver2.rest_service.ISignupService;
import com.kiwi.auready_ver2.rest_service.MockFailedSignupService;
import com.kiwi.auready_ver2.rest_service.MockSignupService;
import com.kiwi.auready_ver2.rest_service.SignupInfo;
import com.kiwi.auready_ver2.rest_service.SignupResponse;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.mock.BehaviorDelegate;
import retrofit2.mock.MockRetrofit;
import retrofit2.mock.NetworkBehavior;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;

/**
 * Created by kiwi on 6/12/16.
 */
public class LoginPresenter_LoginTest {


    private LoginPresenter mLoginPresenter;

    @Mock
    private LoginContract.View mLoginView;

    private MockRetrofit mockRetrofit;
    private Retrofit retrofit;

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);

        mLoginPresenter = new LoginPresenter(mLoginView);

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
    public void showSetLoginSuccessUi_whenLoginSucceed() {

        // Create the loginInfo stub
        String email = "dd@gmail.com";
        String password = "123";
        ClientCredentials newCredentials = new ClientCredentials(
                ClientCredentials.CLIENT_ID,
                ClientCredentials.GRANT_TYPE,
                email,
                password);

        // Request login to Server
        mLoginPresenter.requestLogin(email, password);

        Response<TokenInfo> loginResponse = null;
//        loginResponse = executeMockLoginService();

//        // Succeed to request signup
//        if (loginResponse != null && loginResponse.isSuccessful()) {
//
//            mLoginPresenter.onLoginSuccess(loginResponse.body());
//            verify(mLoginView).setLoginSuccessUI(loginResponse.body());
//        }
    }


    private Response<SignupResponse> executeMockSignupService() throws IOException {

        // Create the signupInfo stub
        String email = "dd@gmail.com";
        String password = "123";

        /*------------------------------------------------------------------------------------*/
        // q This is the mock webserver. how to separate it from here?
        // Execute mock webserver
        BehaviorDelegate<ISignupService> delegate = mockRetrofit.create(ISignupService.class);
        ISignupService mockSignupService = new MockSignupService(delegate);

        SignupInfo signupInfo = new SignupInfo(email, password);
        Call<SignupResponse> signupCall = mockSignupService.signupLocal(signupInfo);
        Response<SignupResponse> signupResponse = signupCall.execute();
        /*------------------------------------------------------------------------------------*/

        return signupResponse;
    }

    private Response<SignupResponse> executeMockFailedSignupService() throws IOException {
        // Create the signupInfo stub
        String email = "bbb@bbb.bbb";
        String password = "123";

        BehaviorDelegate<ISignupService> delegate = mockRetrofit.create(ISignupService.class);
        MockFailedSignupService mockSignupService = new MockFailedSignupService(delegate);

        SignupInfo signupInfo = new SignupInfo(email, password);

        Call<SignupResponse> signupCall = mockSignupService.signupLocal(signupInfo);
        Response<SignupResponse> signupResponse = signupCall.execute();

        return signupResponse;
    }
//
//    @Test
//    public void showSignupFailMessage_whenEmailAndPasswordIsInvalid() throws IOException {
//
//        // Request signup to server with invalid credentials
//        String email = "bbb@bbb.bbb";
//        String password = "123";
//
//        mLoginPresenter.requestSignup(email, password);
//
//        Response<SignupResponse> signupResponse = null;
//        try {
//            signupResponse = executeMockFailedSignupService();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        if (signupResponse != null && signupResponse.code() != 404) {
//
//            // Failed to request signup
//            // error code is 404, reason: email is already registered.
//            mLoginPresenter.onSignupFail(R.string.signup_fail_message_404);
//            verify(mLoginView).showSignupFailMessage(R.string.signup_fail_message_404);
//        }
//    }


}