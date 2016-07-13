package com.kiwi.auready_ver2.login;

/**
 * Created by kiwi on 6/11/16.
 */
public interface SignupContract {

    interface View {

        void showEmailError(int resourceId);

        void setSignupSuccessUI(String email);
        void setSignupFailMessage(int resourceId);

        void showPasswordError(int resourceId);
    }

    interface Presenter {

        boolean validateEmail(String email);
        boolean validatePassword(String password);

        void requestSignup(String email, String password);

        // After request Signup to Server
        void onSignupSuccess(String email);
        void onSignupFail(int stringResource);

        void attemptSignup(String email, String password);

        void onEmailError(int stringResource);
        void onPasswordError(int stringResource);
    }
}
