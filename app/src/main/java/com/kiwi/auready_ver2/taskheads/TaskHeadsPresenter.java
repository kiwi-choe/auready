package com.kiwi.auready_ver2.taskheads;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.login.LoginActivity;
import com.kiwi.auready_ver2.taskheads.domain.usecase.DeleteTaskHead;
import com.kiwi.auready_ver2.taskheads.domain.usecase.GetTaskHeads;
import com.kiwi.auready_ver2.tasks.TasksActivity;
import com.kiwi.auready_ver2.taskheads.domain.usecase.SaveTaskHead;
import com.kiwi.auready_ver2.util.LoginUtils;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by kiwi on 6/26/16.
 */
public class TaskHeadsPresenter implements TaskHeadsContract.Presenter {

    private final TaskHeadsContract.View mTaskHeadView;

    private UseCaseHandler mUseCaseHandler;
    private final GetTaskHeads mGetTaskHeads;
    private final DeleteTaskHead mDeleteTaskHead;
    private final SaveTaskHead mSaveTaskHead;

    public TaskHeadsPresenter(UseCaseHandler useCaseHandler, @NonNull TaskHeadsContract.View tasksView,
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

        Log.d("kiwi_test", "requestCode = " + String.valueOf(requestCode));
        Log.d("kiwi_test", "resultCode = " + String.valueOf(resultCode));

        if (TaskHeadsActivity.REQ_LOGINOUT == requestCode && Activity.RESULT_OK == resultCode) {

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

        if(TaskHeadsActivity.REQ_ADD_TASK == requestCode && Activity.RESULT_OK == resultCode) {

            Log.d("kiwi_test", "entered in REQ_ADD_TASK");
            boolean isEmptyTasks = data.getBooleanExtra(TasksActivity.EXTRA_ISEMPTY_TASKHEAD, false);
            String taskHeadId = data.getStringExtra(TaskHeadsActivity.EXTRA_TASKHEAD_ID);
            String taskHeadTitle = data.getStringExtra(TaskHeadsActivity.EXTRA_TASKHEAD_TITLE);
            if(isEmptyTasks) {
                Log.d("kiwi_test", "entered in isEmptyTasks");
                mTaskHeadView.showEmptyTaskHeadError();
//                deleteTaskHead(taskHeadId);
                deleteTaskHeadByIsEmptyTaskHead(taskHeadId);
            } else {

                saveTaskHead(taskHeadId, taskHeadTitle);
            }
        }
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
    public void deleteTaskHead(@NonNull String taskHeadId) {

        mUseCaseHandler.execute(mDeleteTaskHead, new DeleteTaskHead.RequestValues(taskHeadId),
                new UseCase.UseCaseCallback<DeleteTaskHead.ResponseValue>() {

                    @Override
                    public void onSuccess(DeleteTaskHead.ResponseValue response) {
//                        mTaskHeadView.showTaskHeadDeleted();
                        loadTaskHeads();
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    @Override
    public void deleteTaskHeadByIsEmptyTaskHead(String taskHeadId) {

        mUseCaseHandler.execute(mDeleteTaskHead, new DeleteTaskHead.RequestValues(taskHeadId),
                new UseCase.UseCaseCallback<DeleteTaskHead.ResponseValue>() {

                    @Override
                    public void onSuccess(DeleteTaskHead.ResponseValue response) {
//                        loadTaskHeads();
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    @Override
    public void saveTaskHead(String taskHeadId, String title) {
        if(taskHeadId == null || taskHeadId.isEmpty()) {
            createTaskHead();
        }
        else {
            updateTaskHead(taskHeadId, title);
        }
    }

    private void createTaskHead() {

        final TaskHead newTaskHead = new TaskHead();
        mUseCaseHandler.execute(mSaveTaskHead, new SaveTaskHead.RequestValues(newTaskHead),
                new UseCase.UseCaseCallback<SaveTaskHead.ResponseValue>() {
                    @Override
                    public void onSuccess(SaveTaskHead.ResponseValue response) {

                        // open AddEditView
                        mTaskHeadView.openTasks(newTaskHead);
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    private void updateTaskHead(String taskHeadId, String title) {

        Log.d("kiwi_test", "entered in updateTaskHead()");

        if(taskHeadId == null) {
            throw new RuntimeException("updateTask() was called but taskHead is new.");
        }
        final TaskHead taskHead = new TaskHead(taskHeadId, title);
        mUseCaseHandler.execute(mSaveTaskHead, new SaveTaskHead.RequestValues(taskHead),
                new UseCase.UseCaseCallback<SaveTaskHead.ResponseValue>() {

                    @Override
                    public void onSuccess(SaveTaskHead.ResponseValue response) {
                        loadTaskHeads();
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    @Override
    public void editTaskHead(@NonNull TaskHead requestedTaskHead) {
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
