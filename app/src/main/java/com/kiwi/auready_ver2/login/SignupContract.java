package com.kiwi.auready_ver2.login;

import com.kiwi.auready_ver2.data.Friend;

/**
 * Created by kiwi on 6/11/16.
 */
public interface SignupContract {

    interface View {

        void showEmailError(int resourceId);

        void setSignupSuccessUI(String email, String name);
        void showSignupFailMessage(int resourceId);

        void showPasswordError(int resourceId);
    }

    interface Presenter {

        boolean validateEmail(String email);
        boolean validatePassword(String password);

        void requestSignup(String email, String password, String name);

        // After request Signup to Server
        void onSignupSuccess(String email, String name);
        void onSignupFail(int stringResource);

        void attemptSignup(String email, String password, String name);

        void onEmailError(int stringResource);
        void onPasswordError(int stringResource);
    }
}
