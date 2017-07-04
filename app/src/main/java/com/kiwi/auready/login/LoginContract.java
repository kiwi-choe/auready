package com.kiwi.auready.login;

import com.kiwi.auready.BasePresenter;
import com.kiwi.auready.BaseView;
import com.kiwi.auready.rest_service.login.LoginResponse;

/**
 * Contract of LoginView and Presenter
 */
public interface LoginContract {

    interface View extends BaseView<Presenter> {

        void showEmailError(int resourceId);
        void showPasswordError(int resourceId);

        void setLoginSuccessUI(String userEmail, String userName);

        void showLoginFailMessage(int stringResource);

        void setLoggedInUserInfo(String accessToken, String email, String name, String remote_userid);
    }

    interface Presenter extends BasePresenter {

        boolean validateEmail(String email);
        boolean validatePassword(String password);
        void attemptLogin(String email, String password);

        void onEmailError(int stringResource);
        void onPasswordError(int stringResource);

        void onLoginSuccess(LoginResponse loginResponse);

        void onLoginFail(int stringResource);

        void attemptSocialLogin(String socialapp, String idToken);
    }
}
