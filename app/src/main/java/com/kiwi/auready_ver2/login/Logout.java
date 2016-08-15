package com.kiwi.auready_ver2.login;

import android.util.Log;

import com.kiwi.auready_ver2.data.source.local.AccessTokenStore;
import com.kiwi.auready_ver2.rest_service.ILoginService;
import com.kiwi.auready_ver2.rest_service.ServiceGenerator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
