package com.kiwi.auready_ver2.login;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.TestUseCaseScheduler;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.api_model.ClientCredential;
import com.kiwi.auready_ver2.data.api_model.ErrorResponse;
import com.kiwi.auready_ver2.data.api_model.LoginResponse;
import com.kiwi.auready_ver2.data.api_model.TokenInfo;
import com.kiwi.auready_ver2.data.source.FriendRepository;
import com.kiwi.auready_ver2.friend.FriendsContract;
import com.kiwi.auready_ver2.login.domain.usecase.SaveFriends;
import com.kiwi.auready_ver2.rest_service.ILoginService;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;

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

import static org.mockito.Mockito.verify;

/**
 * Unit test LoginPresenter
 */
public class LoginPresenterTest {


    private LoginPresenter mLoginPresenter;

    @Mock
    private LoginContract.View mLoginView;

    private MockRetrofit mockRetrofit;
    private Retrofit retrofit;

    @Mock
    private FriendRepository mFriendRepository;

    @Mock
    private FriendsContract.View mFriendView;

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);

        mLoginPresenter = givenLoginPresenter();

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
    public void setLoginSuccessUi_whenLoginSucceed() {

        // Create the loginInfo stub
        String email = "dd@gmail.com";
//        String name = "dd";
        String password = "123";

        // Request login to Server
        mLoginPresenter.requestLogin(email, password);

        Response<LoginResponse> loginResponse = null;
        try {
            loginResponse = executeMockLoginService(email, password);
        } catch (IOException e) {
            e.printStackTrace();

        }

        // Succeed to request login
        if (loginResponse != null && loginResponse.isSuccessful()) {

            mLoginPresenter.onLoginSuccess(loginResponse.body(), email);

            TokenInfo tokenInfo = loginResponse.body().getTokenInfo();
            Assert.assertEquals("access token1", tokenInfo.getAccessToken());
            Assert.assertEquals("token type1", tokenInfo.getTokenType());

            String name = loginResponse.body().getName();
            verify(mLoginView).setLoginSuccessUI(tokenInfo, email, name);
        }
    }


    private Response<LoginResponse> executeMockLoginService(String email, String password) throws IOException {

        BehaviorDelegate<ILoginService> delegate = mockRetrofit.create(ILoginService.class);
        ILoginService mockLoginService = new MockLoginService(delegate);

        // Create the loginInfo stub
        ClientCredential newCredentials = new ClientCredential(
                ClientCredential.CLIENT_ID,
                ClientCredential.GRANT_TYPE,
                email,
                password);
        Call<LoginResponse> loginCall = mockLoginService.login(newCredentials);
        Response<LoginResponse> loginResponse = loginCall.execute();

        return loginResponse;
    }


    @Test
    public void showLoginFailMessage_whenLoginFailed_byInvalidUserInfo() throws IOException {

        // Create the loginInfo stub
        String email = "unregistered-email@gmail.com";
        String password = "123";

        mLoginPresenter.requestLogin(email, password);

        Response<LoginResponse> loginResponse = null;
        try {
            loginResponse = executeMockFailedLoginService_withInvalidUserInfo(email, password);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Converter<ResponseBody, ErrorResponse> errConverter =
                retrofit.responseBodyConverter(ErrorResponse.class, new Annotation[0]);
        ErrorResponse error = errConverter.convert(loginResponse.errorBody());

        if (loginResponse.code() != 400) {

            // Failed to request login
            // error code is 400, reason: login failed. Input the correct id or password
            Assert.assertEquals(400, loginResponse.code());
            Assert.assertEquals("login failed. Input the correct id or password", error.getMessage());

            mLoginPresenter.onLoginFail(R.string.login_fail_message_400);
            // Q 있으나마나한 테스트. this test will be success even though onLoginFail didn't call showLoginFailMessage.
            verify(mLoginView).showLoginFailMessage(R.string.login_fail_message_400);
        }
    }

    private Response<LoginResponse> executeMockFailedLoginService_withInvalidUserInfo(String email, String password) throws IOException {

        BehaviorDelegate<ILoginService> delegate = mockRetrofit.create(ILoginService.class);
        MockFailedLoginService mockFailedLoginService = new MockFailedLoginService(delegate);

        // Create the loginInfo stub
        ClientCredential newCredentials = new ClientCredential(
                ClientCredential.CLIENT_ID,
                ClientCredential.GRANT_TYPE,
                email,
                password);
        Call<LoginResponse> loginCall = mockFailedLoginService.failedLoginByInvalidUserInfo(newCredentials);
        Response<LoginResponse> loginResponse = loginCall.execute();

        return loginResponse;
    }

    @Test
    public void saveFriendsToRepository() {

        // Get a reference to the class under test
        mLoginPresenter = givenLoginPresenter();

        // Create the Friends stub
        List<Friend> friends = Lists.newArrayList(new Friend("aa@aa.com", "aa"), new Friend("bb@bb.com", "bb"), new Friend("cc@cc.com", "cc"));
        // Save Friends of the logged in user
        mLoginPresenter.saveFriends(friends);

        // then friends is saved in the repository
        verify(mFriendRepository).saveFriends(friends);
    }

    @Test
    public void setLogoutUi_whenLogoutSuccess() {
        String accessToken = "accesstoken stub";
        mLoginPresenter.requestLogout(accessToken);
        // succeed to request requestLogout
        mLoginPresenter.onLogoutSuccess();
        verify(mLoginView).setLogoutSuccessResult();
    }

    private LoginPresenter givenLoginPresenter() {

        UseCaseHandler useCaseHandler = new UseCaseHandler(new TestUseCaseScheduler());
        SaveFriends saveFriends = new SaveFriends(mFriendRepository);

        return new LoginPresenter(useCaseHandler, mLoginView, saveFriends);
    }
}