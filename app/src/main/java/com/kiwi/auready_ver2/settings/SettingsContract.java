package com.kiwi.auready_ver2.settings;

/**
 * Contract of SettingsView and SettingsPresenter
 */

public class SettingsContract {

    interface View {

        void setPresenter(Presenter presenter);

        void clearUserInfoInLocalAndShowAccountView();

        void showLogoutFailMessage();
    }

    interface Presenter {

        void logout(String accessToken);

        void onLogoutSuccess();
        void onLogoutFail();
    }
}
