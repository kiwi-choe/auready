package com.kiwi.auready_ver2.login;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.rest_service.login.ClientCredential;
import com.kiwi.auready_ver2.rest_service.login.LoginResponse;
import com.kiwi.auready_ver2.rest_service.login.ILoginService;
import com.kiwi.auready_ver2.rest_service.ServiceGenerator;
import com.kiwi.auready_ver2.util.LoginUtils;

import java.util.regex.Matcher;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by kiwi on 6/11/16.
 */
public class LoginPresenter implements LoginContract.Presenter {

    private static final String TAG = "TAG_LoginPresenter";

    private final LoginContract.View mLoginView;

    private final UseCaseHandler mUseCaseHandler;

    public LoginPresenter(@NonNull UseCaseHandler useCaseHandler,
                          @NonNull LoginContract.View loginView) {
        mUseCaseHandler = checkNotNull(useCaseHandler, "useCaseHandler cannot be null");
        mLoginView = checkNotNull(loginView, "loginView cannot be null");

        mLoginView.setPresenter(this);
    }

    @Override
    public boolean validateEmail(String email) {

        if (email == null || TextUtils.isEmpty(email)) {
            onEmailError(R.string.email_empty_err);
            return false;
        }
        // Check email format
        Matcher matcher = LoginUtils.VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        if (!matcher.find()) {
            onEmailError(R.string.email_format_err);
            return false;
        }

        return true;
    }

    @Override
    public boolean validatePassword(String password) {

        if (password == null || password.isEmpty()) {
            onPasswordError(R.string.password_empty_err);
            return false;
        }
        return true;
    }

    @Override
    public void onEmailError(int stringResourceName) {
        mLoginView.showEmailError(stringResourceName);
    }

    @Override
    public void onPasswordError(int stringResourceName) {
        mLoginView.showPasswordError(stringResourceName);
    }

    @Override
    public void attemptLogin(String email, String password) {

        if (validateEmail(email) && validatePassword(password)) {
            requestLogin(email, password);
        }
    }

    private void requestLogin(final String email, String password) {

        ILoginService loginService =
                ServiceGenerator.createService(ILoginService.class);

        ClientCredential newCredentials = new ClientCredential(
                ClientCredential.CLIENT_SECRET,
                ClientCredential.CLIENT_ID,
                ClientCredential.GRANT_TYPE,
                email,
                password);

        Call<LoginResponse> call = loginService.login(newCredentials);
        call.enqueue(new Callback<LoginResponse>() {

            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    // Save tokenInfo to sharedPreferences
                    onLoginSuccess(response.body(), email);
                } else if (response.code() == 400) {
                    onLoginFail(R.string.login_fail_message_400);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {

                Log.d("Exception in Signup: ", "Called OnFailure()", t);
                onLoginFail(R.string.login_fail_message_onfailure);
            }
        });
    }

    @Override
    public void onLoginSuccess(LoginResponse loginResponse, String loggedInEmail) {

        // send logged in email to MainView
        String name = loginResponse.getUserName();
        mLoginView.setLoginSuccessUI(loggedInEmail, name);

        // Set LoggedInUser info to SharedPreferences
//        void setLoggedInUserInfo(TokenInfo tokenInfo, String email, String name, String myIdOfFriend);
        mLoginView.setLoggedInUserInfo(
                loginResponse.getAccessToken(),
                loggedInEmail,
                name);
    }

    @Override
    public void onLoginFail(int stringResource) {
        mLoginView.showLoginFailMessage(stringResource);
    }

    @Override
    public void requestLogout(String accessToken) {

        ILoginService loginService =
                ServiceGenerator.createService(ILoginService.class, accessToken);

        Call<Void> call = loginService.logout();
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    onLogoutSuccess();
                } else {
                    onLogoutFail();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("Exception in Logout: ", "onFailure()", t);
                onLogoutFail();
            }
        });
    }

    @Override
    public void onLogoutSuccess() {
        mLoginView.setLogoutSuccessResult();
    }

    @Override
    public void onLogoutFail() {
        mLoginView.setLogoutFailResult();
    }

    private void showSaveError() {
        // Show error, log, etc when save is failed.
        Log.d("ErrorInLoginPresenter", "Failed to save the friends");
    }

    @Override
    public void start() {

    }
}
