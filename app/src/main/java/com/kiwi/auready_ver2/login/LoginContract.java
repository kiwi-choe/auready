package com.kiwi.auready_ver2.login;

import com.kiwi.auready_ver2.data.api_model.TokenInfo;

/**
 * Created by kiwi on 6/11/16.
 */
public interface LoginContract {

    interface View {

        void showEmailError(int resourceId);
        void showPasswordError(int resourceId);

        void setLoginSuccessUI(String loggedInEmail);

        void showLoginFailMessage(int stringResource);
    }

    interface Presenter {

        boolean validateEmail(String email);
        boolean validatePassword(String password);
        void attemptLogin(String email, String password);

        void onEmailError(int stringResource);
        void onPasswordError(int stringResource);

        void requestLogin(String email, String password);

        void onLoginSuccess(TokenInfo tokenInfo, String loggedInEmail);

        void onLoginFail(int stringResource);
    }
}
