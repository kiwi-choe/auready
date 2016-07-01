package com.kiwi.auready_ver2.login;

/**
 * Created by kiwi on 6/11/16.
 */
public interface LoginContract {

    interface View {

        void showSignupFailMessage(int stringResourceName);
        void setSignupSuccessUI(String email);

        void showEmailError(int resourceId);
        void showPasswordError(int resourceId);

        void setLoginSuccessUI(String loggedInEmail);

        void showLoginFailMessage(int stringResource);
    }

    interface UserActionsListener {

        boolean validateEmail(String email);
        boolean validatePassword(String password);

        void requestSignup(String email, String password);

        // After request Signup to Server
        void onSignupSuccess(String email);
        void onSignupFail(int stringResource);

        void attemptSignup(String email, String password);
        void attemptLogin(String email, String password);

        void onEmailError(int stringResource);
        void onPasswordError(int stringResource);

        void requestLogin(String email, String password);

        void onLoginSuccess(TokenInfo tokenInfo, String loggedInEmail);

        void onLoginFail(int stringResource);
    }
}
