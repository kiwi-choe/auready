package com.kiwi.auready_ver2.login;

import android.text.TextUtils;
import android.util.Log;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.rest_service.ILoginService;
import com.kiwi.auready_ver2.rest_service.ServiceGenerator;
import com.kiwi.auready_ver2.util.LoginUtil;

import java.util.regex.Matcher;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by kiwi on 6/11/16.
 */
public class LoginPresenter implements LoginContract.Presenter {

    private static final String TAG = "TAG_LoginPresenter";

    private final LoginContract.View mLoginView;

    public LoginPresenter(LoginContract.View loginView) {
        mLoginView = loginView;
    }

    @Override
    public boolean validateEmail(String email) {

        if(email == null || TextUtils.isEmpty(email)) {
            onEmailError(R.string.email_empty_err);
            return false;
        }
        // Check email format
        Matcher matcher = LoginUtil.VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        if(!matcher.find()) {
            onEmailError(R.string.email_format_err);
            return false;
        }

        return true;
    }

    @Override
    public boolean validatePassword(String password) {

        if(password == null || password.isEmpty()) {
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
        if(validateEmail(email) && validatePassword(password))
            requestLogin(email, password);
    }

    @Override
    public void requestLogin(final String email, String password) {

        ILoginService loginService =
                ServiceGenerator.createService(ILoginService.class);

        ClientCredentials newCredentials = new ClientCredentials(
                ClientCredentials.CLIENT_ID,
                ClientCredentials.GRANT_TYPE,
                email,
                password);

        Call<TokenInfo> call = loginService.login(newCredentials);
        call.enqueue(new Callback<TokenInfo>() {

            @Override
            public void onResponse(Call<TokenInfo> call, Response<TokenInfo> response) {
                if (response.isSuccessful()) {
                    // Save tokenInfo to sharedPreferences
                    onLoginSuccess(response.body(), email);
                } else if (response.code() == 404) {
                    onLoginFail(R.string.login_fail_message_404);
                }
            }

            @Override
            public void onFailure(Call<TokenInfo> call, Throwable t) {

                Log.d("Exception in Signup: ", "Called OnFailure()", t);
                onLoginFail(R.string.login_fail_message_onfailure);
            }
        });
    }

    @Override
    public void onLoginSuccess(TokenInfo tokenInfo, String loggedInEmail) {

        // Save tokenInfo to SharedPreferences

        // and send logged in email to MainView
        mLoginView.setLoginSuccessUI(loggedInEmail);
    }

    @Override
    public void onLoginFail(int stringResource) {
        mLoginView.showLoginFailMessage(stringResource);
    }
}
