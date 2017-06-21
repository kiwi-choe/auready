package com.kiwi.auready_ver2.settings;

import android.support.annotation.NonNull;
import android.util.Log;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.rest_service.ServiceGenerator;
import com.kiwi.auready_ver2.rest_service.login.ILoginService;
import com.kiwi.auready_ver2.taskheads.domain.usecase.InitializeLocalData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Settings presenter; account, notifications
 */

public class SettingsPresenter implements SettingsContract.Presenter {

    private UseCaseHandler mUseCaseHandler;
    private final SettingsContract.View mView;
    private final InitializeLocalData mInitializeLocalData;

    public SettingsPresenter(@NonNull UseCaseHandler useCaseHandler,
                             @NonNull SettingsContract.View view,
                             InitializeLocalData initializeLocalData) {
        mUseCaseHandler = useCaseHandler;
        mView = view;
        mInitializeLocalData = initializeLocalData;

        mView.setPresenter(this);
    }

    @Override
    public void logout(String accessToken) {
        checkNotNull(accessToken);

        Log.d("Tag_logout", "accessToken - " + accessToken);
        ILoginService loginService =
                ServiceGenerator.createService(ILoginService.class, accessToken);

        Call<Void> call = loginService.logout(accessToken);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    onLogoutSuccess();
                } else {
                    onLogoutFail();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("Exception in Logout: ", "onFailure()", t);
                onLogoutFail();
            }
        });
    }

    @Override
    public void onLogoutSuccess() {
        mView.clearUserInfoInLocalAndShowAccountView();

        mUseCaseHandler.execute(mInitializeLocalData, new InitializeLocalData.RequestValues(),
                new UseCase.UseCaseCallback<InitializeLocalData.ResponseValue>() {

                    @Override
                    public void onSuccess(InitializeLocalData.ResponseValue response) {
                        Log.d("Tag_logout", "initializeLocalData is succeeded");
                    }

                    @Override
                    public void onError() {
                    }
                });
    }

    @Override
    public void onLogoutFail() {
        mView.showLogoutFailMessage();
    }
}
