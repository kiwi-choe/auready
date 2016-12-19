package com.kiwi.auready_ver2.login;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.BasePresenter;
import com.kiwi.auready_ver2.BaseView;
import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.api_model.LoginResponse;
import com.kiwi.auready_ver2.data.api_model.TokenInfo;

import java.util.List;

/**
 * Created by kiwi on 6/11/16.
 */
public interface LoginContract {

    interface View extends BaseView<Presenter> {

        void showEmailError(int resourceId);
        void showPasswordError(int resourceId);

        void setLoginSuccessUI(TokenInfo tokenInfo, String userEmail, String userName);

        void showLoginFailMessage(int stringResource);

        // Logout
        void setLogoutSuccessResult();
        void setLogoutFailResult();
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

        void saveFriends(List<Friend> friends);

        // Logout
        void requestLogout(String accessToken);

        void onLogoutSuccess();
        void onLogoutFail();
    }
}
