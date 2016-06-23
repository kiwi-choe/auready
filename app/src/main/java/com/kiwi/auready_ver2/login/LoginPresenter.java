package com.kiwi.auready_ver2.login;

import android.text.TextUtils;
import android.util.Log;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.rest_service.ISignupService;
import com.kiwi.auready_ver2.rest_service.ServiceGenerator;
import com.kiwi.auready_ver2.rest_service.SignupInfo;
import com.kiwi.auready_ver2.rest_service.SignupResponse;
import com.kiwi.auready_ver2.util.LoginUtil;

import java.util.regex.Matcher;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by kiwi on 6/11/16.
 */
public class LoginPresenter implements LoginContract.UserActionsListener {

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
    public void requestSignup(final String email, String password) {

        SignupInfo signupInfo = new SignupInfo(email, password);

        ISignupService signupService =
                ServiceGenerator.createService(ISignupService.class);

        Call<SignupResponse> call = signupService.signupLocal(signupInfo);
        call.enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {

                if(response.isSuccessful()) {
                    onSignupSuccess(response.body().getEmail());
                } else if(response.code() == R.integer.signup_fail_code_404) {
                    onSignupFail(R.string.signup_fail_message_404);
                }
            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                Log.d(TAG, "Signup is failed: " + t.getMessage());
                onSignupFail(R.string.signup_fail_message);
            }
        });
    }

    @Override
    public void onSignupSuccess(String email) {
        mLoginView.setSignupSuccessUI(email);
    }

    @Override
    public void onSignupFail(int stringResourceName) {
        mLoginView.showSignupFailMessage(stringResourceName);
    }

    @Override
    public void attemptSignup(String email, String password) {

        if(validateEmail(email) && validatePassword(password))
            requestSignup(email, password);
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
    public void requestLogin(String email, String password) {

        ClientCredentials newCredentials = new ClientCredentials(
                ClientCredentials.CLIENT_ID,
                ClientCredentials.GRANT_TYPE,
                email,
                password);

        // request auth


    }

    @Override
    public void onLoginSuccess(TokenInfo tokenInfo) {

    }
}
