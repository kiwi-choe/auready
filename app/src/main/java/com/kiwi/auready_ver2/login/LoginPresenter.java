package com.kiwi.auready_ver2.login;

import com.kiwi.auready_ver2.rest_service.ServiceGenerator;
import com.kiwi.auready_ver2.rest_service.SignupInfo;
import com.kiwi.auready_ver2.rest_service.ISignupService;
import com.kiwi.auready_ver2.rest_service.SignupResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by kiwi on 6/11/16.
 */
public class LoginPresenter implements LoginContract.UserActionsListener {

    private final LoginContract.View mLoginView;

    public LoginPresenter(LoginContract.View loginView) {
        mLoginView = loginView;
    }


    @Override
    public void requestLogin() {

    }

    @Override
    public void requestSignup() {

    }

    @Override
    public void onLoginSuccess() {

    }

    @Override
    public void onLoginFail() {

    }

    @Override
    public boolean validateEmail(String email) {

        if(email == null || email.isEmpty()) {
            mLoginView.showEmailError();
            return false;
        }

        return true;
    }

    @Override
    public boolean validatePassword(String password) {
        if(password == null || password.isEmpty()) {
            mLoginView.showPasswordError();
            return false;
        }
        return true;
    }

    @Override
    public void onRequestSignup(String email, String password) {

        SignupInfo signupInfo = new SignupInfo(email, password);

        ISignupService signupService =
                ServiceGenerator.createService(ISignupService.class);

        Call<SignupResponse> call = signupService.signupLocal(signupInfo);
        call.enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                if(response.isSuccessful()) {

                }
            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void onSignupSuccess(String email) {
        mLoginView.setSignupSuccessUI(email);
    }

    @Override
    public void onSignupFail() {
        mLoginView.showSignupFailMessage();
    }

    @Override
    public boolean validateAccountCredentials(String email, String password) {

        //onRequestSignup(email, password);
        return false;
    }
}
