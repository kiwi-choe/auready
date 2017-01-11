package com.kiwi.auready_ver2.login;

import com.kiwi.auready_ver2.BasePresenter;
import com.kiwi.auready_ver2.BaseView;
import com.kiwi.auready_ver2.data.api_model.LoginResponse;
import com.kiwi.auready_ver2.data.api_model.TokenInfo;

/**
 * Created by kiwi on 6/11/16.
 */
public interface LoginContract {

    interface View extends BaseView<Presenter> {

        void showEmailError(int resourceId);
        void showPasswordError(int resourceId);

        void setLoginSuccessUI(String userEmail, String userName);

        void showLoginFailMessage(int stringResource);

        // Logout
        void setLogoutSuccessResult();
        void setLogoutFailResult();

        void setLoggedInUserInfo(TokenInfo tokenInfo, String email, String name, String myIdOfFriend);
    }

    interface Presenter extends BasePresenter {

        boolean validateEmail(String email);
        boolean validatePassword(String password);
        void attemptLogin(String email, String password);

        void onEmailError(int stringResource);
        void onPasswordError(int stringResource);

        void requestLogin(String email, String password);

        void onLoginSuccess(LoginResponse loginResponse, String loggedInEmail);

        void onLoginFail(int stringResource);

        // Logout
        void requestLogout(String accessToken);

        void onLogoutSuccess();
        void onLogoutFail();
    }
}
