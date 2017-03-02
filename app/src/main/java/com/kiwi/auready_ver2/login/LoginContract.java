package com.kiwi.auready_ver2.login;

import com.kiwi.auready_ver2.BasePresenter;
import com.kiwi.auready_ver2.BaseView;
import com.kiwi.auready_ver2.rest_service.login.LoginResponse;

/**
 * Contract of LoginView and Presenter
 */
public interface LoginContract {

    interface View extends BaseView<Presenter> {

        void showEmailError(int resourceId);
        void showPasswordError(int resourceId);

        void setLoginSuccessUI(String userEmail, String userName);

        void showLoginFailMessage(int stringResource);

        void setLoggedInUserInfo(String accessToken, String email, String name);
    }

    interface Presenter extends BasePresenter {

        boolean validateEmail(String email);
        boolean validatePassword(String password);
        void attemptLogin(String email, String password);

        void onEmailError(int stringResource);
        void onPasswordError(int stringResource);

        void onLoginSuccess(LoginResponse loginResponse, String loggedInEmail);

        void onLoginFail(int stringResource);
    }
}
