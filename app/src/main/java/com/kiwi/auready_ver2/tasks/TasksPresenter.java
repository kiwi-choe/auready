package com.kiwi.auready_ver2.tasks;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.login.LoginActivity;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by kiwi on 6/26/16.
 */
public class TasksPresenter implements TasksContract.Presenter {

    private final TasksContract.View mTasksView;

    public TasksPresenter(@NonNull TasksContract.View tasksView) {

        mTasksView = checkNotNull(tasksView, "tasksView cannot be null!");

        mTasksView.setPresenter(this);
    }

    @Override
    public void result(int requestCode, int resultCode, Intent data) {

        if(LoginActivity.REQ_LOGIN == requestCode
                && Activity.RESULT_OK == resultCode) {

            mTasksView.setLoginSuccessUI(data.getStringExtra(LoginActivity.REGISTERED_EMAIL));
        }
    }
}
