package com.kiwi.auready_ver2.taskheads;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.login.LoginActivity;
import com.kiwi.auready_ver2.taskheads.domain.usecase.GetTaskHeads;
import com.kiwi.auready_ver2.util.LoginUtils;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by kiwi on 6/26/16.
 */
public class TaskHeadPresenter implements TaskHeadContract.Presenter {

    private final TaskHeadContract.View mTasksView;

    private final GetTaskHeads mGetTaskHeads;
    private UseCaseHandler mUseCaseHandler;

    public TaskHeadPresenter(UseCaseHandler useCaseHandler, @NonNull TaskHeadContract.View tasksView, GetTaskHeads getTaskHeads) {

        mUseCaseHandler = checkNotNull(useCaseHandler, "useCaseHandler cannot be null");
        mTasksView = checkNotNull(tasksView, "tasksView cannot be null!");
        mGetTaskHeads = checkNotNull(getTaskHeads, "getTaskHeads cannot be null");

        mTasksView.setPresenter(this);
    }

    @Override
    public void result(int requestCode, int resultCode, Intent data) {

        if (LoginActivity.REQ_LOGINOUT == requestCode && Activity.RESULT_OK == resultCode) {
            int loginOrOut = data.getIntExtra(LoginUtils.LOGIN_LOGOUT, 10);
            boolean isSuccess = data.getBooleanExtra(LoginUtils.IS_SUCCESS, false);
            if (loginOrOut == LoginUtils.LOGIN) {
                if (isSuccess) {
                    mTasksView.setLoginSuccessUI();
                }
                else {

                }
            }
            else if (loginOrOut == LoginUtils.LOGOUT) {
                if (isSuccess) {

                }
                else {

                }
            }
        }
    }

    @Override
    public void addNewTask() {
        // open AddEditView
        mTasksView.openAddEditTask();
    }

    @Override
    public void openTask(TaskHead clickedTaskHead) {

    }

    @Override
    public void loadTaskHeads() {

        mUseCaseHandler.execute(mGetTaskHeads, new GetTaskHeads.RequestValues(),
                new UseCase.UseCaseCallback<GetTaskHeads.ResponseValue>() {

                    @Override
                    public void onSuccess(GetTaskHeads.ResponseValue response) {
                        List<TaskHead> taskHeads = response.getTaskHeads();
                        processTasks(taskHeads);
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    private void processTasks(List<TaskHead> taskHeads) {
        if(taskHeads.isEmpty()) {
            mTasksView.showNoTaskHeads();
        } else {
            mTasksView.showTaskHeads(taskHeads);
        }
    }
}
