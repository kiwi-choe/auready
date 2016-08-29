package com.kiwi.auready_ver2.taskheads;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.login.LoginActivity;
import com.kiwi.auready_ver2.taskheads.domain.usecase.DeleteTaskHead;
import com.kiwi.auready_ver2.taskheads.domain.usecase.GetTaskHeads;
import com.kiwi.auready_ver2.tasks.domain.usecase.SaveTaskHead;
import com.kiwi.auready_ver2.util.LoginUtils;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by kiwi on 6/26/16.
 */
public class TaskHeadPresenter implements TaskHeadContract.Presenter {

    private final TaskHeadContract.View mTaskHeadView;

    private UseCaseHandler mUseCaseHandler;
    private final GetTaskHeads mGetTaskHeads;
    private final DeleteTaskHead mDeleteTaskHead;
    private final SaveTaskHead mSaveTaskHead;

    public TaskHeadPresenter(UseCaseHandler useCaseHandler, @NonNull TaskHeadContract.View tasksView,
                             @NonNull GetTaskHeads getTaskHeads, @NonNull DeleteTaskHead deleteTaskHead, SaveTaskHead saveTaskHead) {
        mUseCaseHandler = checkNotNull(useCaseHandler, "useCaseHandler cannot be null");
        mTaskHeadView = checkNotNull(tasksView, "tasksView cannot be null!");
        mGetTaskHeads = checkNotNull(getTaskHeads, "getTaskHeads cannot be null");
        mDeleteTaskHead = checkNotNull(deleteTaskHead, "deleteTaskHead cannot be null");
        mSaveTaskHead = checkNotNull(saveTaskHead, "saveTaskHead cannot be null");

        mTaskHeadView.setPresenter(this);
    }

    @Override
    public void start() {
        loadTaskHeads();
    }

    @Override
    public void result(int requestCode, int resultCode, Intent data) {

        if (LoginActivity.REQ_LOGINOUT == requestCode && Activity.RESULT_OK == resultCode) {
            int loginOrOut = data.getIntExtra(LoginUtils.LOGIN_LOGOUT, 10);
            boolean isSuccess = data.getBooleanExtra(LoginUtils.IS_SUCCESS, false);
            if (loginOrOut == LoginUtils.LOGIN) {
                if (isSuccess) {
                    mTaskHeadView.setLoginSuccessUI();
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
    public void addNewTaskHead() {

        TaskHead newTaskHead = new TaskHead();
        mUseCaseHandler.execute(mSaveTaskHead, new SaveTaskHead.RequestValues(newTaskHead),
                new UseCase.UseCaseCallback<SaveTaskHead.ResponseValue>() {
                    @Override
                    public void onSuccess(SaveTaskHead.ResponseValue response) {

                    }

                    @Override
                    public void onError() {

                    }
                });

        // open AddEditView
        mTaskHeadView.openTasks();
    }

    @Override
    public void openTaskHead(TaskHead clickedTaskHead) {

    }

    @Override
    public void loadTaskHeads() {

        mUseCaseHandler.execute(mGetTaskHeads, new GetTaskHeads.RequestValues(),
                new UseCase.UseCaseCallback<GetTaskHeads.ResponseValue>() {

                    @Override
                    public void onSuccess(GetTaskHeads.ResponseValue response) {
                        List<TaskHead> taskHeads = response.getTaskHeads();
                        processTaskHeads(taskHeads);
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    @Override
    public void deleteTaskHead(@NonNull TaskHead taskHead) {
        mTaskHeadView.showTaskHeadDeleted();
//
//        String taskHeadId = taskHead.getId();
//        mUseCaseHandler.execute(mDeleteTaskHead, new DeleteTaskHead.RequestValues(taskHeadId),
//                new UseCase.UseCaseCallback<DeleteTaskHead.ResponseValue>() {
//
//                    @Override
//                    public void onSuccess(DeleteTaskHead.ResponseValue response) {
//                        mTaskHeadView.showTaskHeadDeleted();
//                    }
//
//                    @Override
//                    public void onError() {
//
//                    }
//                });
    }

    @Override
    public void editTasks(@NonNull TaskHead requestedTaskHead) {
        checkNotNull(requestedTaskHead, "requestedTaskHead cannot be null");
        mTaskHeadView.openTasks(requestedTaskHead);
    }

    private void processTaskHeads(List<TaskHead> taskHeads) {
        if(taskHeads.isEmpty()) {
            mTaskHeadView.showNoTaskHeads();
        } else {
            mTaskHeadView.showTaskHeads(taskHeads);
        }
    }
}
