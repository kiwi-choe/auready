package com.kiwi.auready_ver2.taskheads;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.login.LoginFragment;
import com.kiwi.auready_ver2.rest_service.ServiceGenerator;
import com.kiwi.auready_ver2.rest_service.login.ILoginService;
import com.kiwi.auready_ver2.taskheaddetail.TaskHeadDetailFragment;
import com.kiwi.auready_ver2.taskheads.domain.usecase.DeleteTaskHeads;
import com.kiwi.auready_ver2.taskheads.domain.usecase.GetTaskHeads;
import com.kiwi.auready_ver2.taskheads.domain.usecase.GetTaskHeadsCount;
import com.kiwi.auready_ver2.taskheads.domain.usecase.UpdateTaskHeadOrders;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by kiwi on 6/26/16.
 */
public class TaskHeadsPresenter implements TaskHeadsContract.Presenter {

    private final TaskHeadsContract.View mTaskHeadView;

    private UseCaseHandler mUseCaseHandler;
    private final GetTaskHeads mGetTaskHeads;
    private final DeleteTaskHeads mDeleteTaskHeads;
    private final GetTaskHeadsCount mGetTaskHeadCount;
    private UpdateTaskHeadOrders mUpdateTaskHeadOrders;

    public TaskHeadsPresenter(UseCaseHandler useCaseHandler, @NonNull TaskHeadsContract.View tasksView,
                              @NonNull GetTaskHeads getTaskHeads,
                              @NonNull DeleteTaskHeads deleteTaskHeads,
                              @NonNull GetTaskHeadsCount getTaskHeadCount,
                              @NonNull UpdateTaskHeadOrders updateTaskHeadOrders) {
        mUseCaseHandler = checkNotNull(useCaseHandler, "useCaseHandler cannot be null");
        mTaskHeadView = checkNotNull(tasksView, "tasksView cannot be null!");
        mGetTaskHeads = checkNotNull(getTaskHeads, "getTaskHeads cannot be null");
        mDeleteTaskHeads = checkNotNull(deleteTaskHeads, "deleteTaskHeads cannot be null");
        mGetTaskHeadCount = checkNotNull(getTaskHeadCount);
        mUpdateTaskHeadOrders = updateTaskHeadOrders;

        mTaskHeadView.setPresenter(this);
    }

    @Override
    public void start() {
        loadTaskHeads();
    }

    @Override
    public void loadTaskHeads() {

        mUseCaseHandler.execute(mGetTaskHeads, new GetTaskHeads.RequestValues(),
                new UseCase.UseCaseCallback<GetTaskHeads.ResponseValue>() {

                    @Override
                    public void onSuccess(GetTaskHeads.ResponseValue response) {
                        mTaskHeadView.showTaskHeads(response.getTaskHeads());
                    }

                    @Override
                    public void onError() {
                        mTaskHeadView.showNoTaskHeads();
                    }
                });
    }

    @Override
    public void addNewTaskHead() {
        mUseCaseHandler.execute(mGetTaskHeadCount, new GetTaskHeadsCount.RequestValues(),
                new UseCase.UseCaseCallback<GetTaskHeadsCount.ResponseValue>() {

                    @Override
                    public void onSuccess(GetTaskHeadsCount.ResponseValue response) {
                        int cntOfTaskHeads = response.getTaskHeadsCount();
                        mTaskHeadView.showTaskHeadDetail(cntOfTaskHeads);
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    @Override
    public void result(int requestCode, int resultCode, Intent data) {
        if (TaskHeadsActivity.REQ_ADD_TASKHEAD == requestCode && Activity.RESULT_OK == resultCode) {
            // Created TaskHead, Open TasksView of this taskhead
            String taskHeadId = "";
            if (data.hasExtra(TaskHeadDetailFragment.EXTRA_TASKHEAD_ID)) {
                taskHeadId = data.getStringExtra(TaskHeadDetailFragment.EXTRA_TASKHEAD_ID);
            }
            String title = "";
            if (data.hasExtra(TaskHeadDetailFragment.EXTRA_TITLE)) {
                title = data.getStringExtra(TaskHeadDetailFragment.EXTRA_TITLE);
            }

            mTaskHeadView.showTasksView(taskHeadId, title);

            // Canceled create taskhead, Open TaskHeadsView
        }
        if (TaskHeadsActivity.REQ_LOGINOUT == requestCode && Activity.RESULT_OK == resultCode) {

            boolean login = data.getBooleanExtra(LoginFragment.LOGIN_LOGOUT, false);
            boolean isSuccess = data.getBooleanExtra(LoginFragment.IS_SUCCESS, false);
            if (login && isSuccess) {
                mTaskHeadView.setLoginSuccessUI();
            } else if (!login && isSuccess) {
                mTaskHeadView.setLogoutSuccessUI();
            }
        }
    }

    @Override
    public void deleteTaskHeads(List<TaskHead> taskheads) {
        checkNotNull(taskheads);
        final List<String> taskHeadIds = new ArrayList<>();
        for (TaskHead taskHead : taskheads) {
            taskHeadIds.add(taskHead.getId());
        }
        mUseCaseHandler.execute(mDeleteTaskHeads, new DeleteTaskHeads.RequestValues(taskHeadIds),
                new UseCase.UseCaseCallback<DeleteTaskHeads.ResponseValue>() {

                    @Override
                    public void onSuccess(DeleteTaskHeads.ResponseValue response) {
                        loadTaskHeads();
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    @Override
    public void updateOrders(List<TaskHead> taskheads) {
        List<TaskHead> updatingTaskHeads = new ArrayList<>(0);
        int size = taskheads.size();
        for(int i = 0; i<size; i++) {
            TaskHead taskHead = taskheads.get(i);
            updatingTaskHeads.add(new TaskHead(taskHead.getId(), taskHead.getTitle(), i));
        }

        mUseCaseHandler.execute(mUpdateTaskHeadOrders, new UpdateTaskHeadOrders.RequestValues(updatingTaskHeads),
                new UseCase.UseCaseCallback<UpdateTaskHeadOrders.ResponseValue>() {

                    @Override
                    public void onSuccess(UpdateTaskHeadOrders.ResponseValue response) {
                        loadTaskHeads();
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    @Override
    public void logout(@NonNull String accessToken) {
        checkNotNull(accessToken);

        ILoginService loginService =
                ServiceGenerator.createService(ILoginService.class, accessToken);

        Call<Void> call = loginService.logout();
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
        mTaskHeadView.setLogoutSuccessUI();
    }

    @Override
    public void onLogoutFail() {
        mTaskHeadView.setLogoutFailResult();
    }
}
