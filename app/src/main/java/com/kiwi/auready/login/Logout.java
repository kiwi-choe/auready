package com.kiwi.auready.login;

import com.kiwi.auready.data.source.local.AccessTokenStore;

/**
 * Created by kiwi on 8/15/16.
 */
public class Logout {
    private final LoginPresenter mLoginPresenter;
    private AccessTokenStore mAccessTokenStore;

    public Logout(LoginPresenter loginPresenter) {
        mLoginPresenter = loginPresenter;
        mAccessTokenStore = AccessTokenStore.getInstance();
    }

    public void logoutUser() {

    }
}
