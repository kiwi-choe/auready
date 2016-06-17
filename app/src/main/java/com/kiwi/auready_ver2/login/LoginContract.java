package com.kiwi.auready_ver2.login;

/**
 * Created by kiwi on 6/11/16.
 */
public interface LoginContract {

    interface View {

        void showSignupFailMessage();
        void setSignupSuccessUI(String email);

        void showEmailError();
        void showPasswordError();

//        void showLoginFailMessage(String errmsg);
//        void showSignupFailMessage(String errmsg);
//
//        void setLoginSuccess();
    }

    interface UserActionsListener {
//
//        void validateCredentialsLogin(String email, String password);
//        void validateCredentialsSignup(String email, String password);

        void requestLogin();
        void requestSignup();
//
        // After request Login to Server
        void onLoginSuccess();
        void onLoginFail();

        boolean validateEmail(String email);
        boolean validatePassword(String password);

        void onRequestSignup(String email, String password);

        // After request Signup to Server
        void onSignupSuccess(String email);
        void onSignupFail();

        boolean validateAccountCredentials(String email, String password);
    }
}
