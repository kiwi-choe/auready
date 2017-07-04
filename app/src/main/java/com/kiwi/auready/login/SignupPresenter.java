package com.kiwi.auready.login;

import android.text.TextUtils;
import android.util.Log;

import com.kiwi.auready.R;
import com.kiwi.auready.rest_service.login.SignupInfo;
import com.kiwi.auready.rest_service.login.SignupResponse;
import com.kiwi.auready.rest_service.login.ISignupService;
import com.kiwi.auready.rest_service.ServiceGenerator;
import com.kiwi.auready.util.LoginUtils;

import java.util.regex.Matcher;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by kiwi on 7/12/16.
 */
public class SignupPresenter implements SignupContract.Presenter {

    private static final String TAG = "TAG_SignupPresenter";

    private static final int SIGNUP_FAIL_404 = 404;

    private final SignupContract.View mSignupView;

    public SignupPresenter(SignupContract.View signupView) {
        mSignupView = signupView;
    }

    @Override
    public boolean validateEmail(String email) {

        if(email == null || TextUtils.isEmpty(email)) {
            onEmailError(R.string.email_empty_err);
            return false;
        }
        // Check email format
        Matcher matcher = LoginUtils.VALID_EMAIL_ADDRESS_REGEX.matcher(email);
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
    public void attemptSignup(String email, String password, String name) {

        if(validateEmail(email) && validatePassword(password)) {

            // Check that edName has string name
            if(name.isEmpty()) {
                String[] result = email.split(LoginUtils.EMAIL_TOKEN);
                name = result[0];
            }
            requestSignup(email, password, name);
        }
    }

    @Override
    public void requestSignup(String email, String password, String name) {

        SignupInfo signupInfo = new SignupInfo(email, name, password);

        ISignupService signupService =
                ServiceGenerator.createService(ISignupService.class);

        Call<SignupResponse> call = signupService.signupLocal(signupInfo);
        call.enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {

                if(response.isSuccessful()) {
                    onSignupSuccess(response.body().getEmail(), response.body().getName());
                } else if(response.code() == SIGNUP_FAIL_404) {
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
    public void onSignupSuccess(String email, String name) {
        mSignupView.setSignupSuccessUI(email, name);
    }

    @Override
    public void onSignupFail(int resourceId) {
        mSignupView.showSignupFailMessage(resourceId);
    }

    @Override
    public void onEmailError(int resourceId) {
        mSignupView.showEmailError(resourceId);
    }

    @Override
    public void onPasswordError(int resourceId) {
        mSignupView.showPasswordError(resourceId);
    }

}
