package com.kiwi.auready_ver2.taskheads;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.taskheaddetail.TaskHeadDetailActivity;
import com.kiwi.auready_ver2.taskheaddetail.TaskHeadDetailFragment;
import com.kiwi.auready_ver2.taskheads.domain.usecase.DeleteTaskHead;
import com.kiwi.auready_ver2.taskheads.domain.usecase.GetTaskHeads;

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

    public TaskHeadsPresenter(UseCaseHandler useCaseHandler, @NonNull TaskHeadsContract.View tasksView,
                              @NonNull GetTaskHeads getTaskHeads, DeleteTaskHead deleteTaskHead) {
        mUseCaseHandler = checkNotNull(useCaseHandler, "useCaseHandler cannot be null");
        mTaskHeadView = checkNotNull(tasksView, "tasksView cannot be null!");
        mGetTaskHeads = checkNotNull(getTaskHeads, "getTaskHeads cannot be null");
        mDeleteTaskHead = checkNotNull(deleteTaskHead, "deleteTaskHead cannot be null");

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
                        List<TaskHead> taskHeads = response.getTaskHeads();
                        processTaskHeads(taskHeads);
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    private void processTaskHeads(List<TaskHead> taskHeads) {
        if(taskHeads.isEmpty()) {
            mTaskHeadView.showNoTaskHeads();
        } else {
            mTaskHeadView.showTaskHeads(taskHeads);
        }
    }

    @Override
    public void deleteTaskHead(String taskHeadId) {
        mUseCaseHandler.execute(mDeleteTaskHead, new DeleteTaskHead.RequestValues(taskHeadId),
                new UseCase.UseCaseCallback<DeleteTaskHead.ResponseValue>() {
                    @Override
                    public void onSuccess(DeleteTaskHead.ResponseValue response) {
                        loadTaskHeads();
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    @Override
    public void addNewTask() {
        mTaskHeadView.showTaskHeadDetail();
    }

    @Override
    public void result(int requestCode, int resultCode, Intent data) {
        if(TaskHeadDetailActivity.REQ_ADD_TASKHEAD == requestCode
                && Activity.RESULT_OK == resultCode) {
            // Created TaskHead, Open TasksView of this taskhead
            if (data.hasExtra(TaskHeadDetailFragment.ARG_TASKHEAD_ID)) {
                String taskHeadId = data.getStringExtra(TaskHeadDetailFragment.ARG_TASKHEAD_ID);
                mTaskHeadView.showTasksView(taskHeadId);
            }
            // Canceled create taskhead, Open TaskHeadsView
        }
    }
}
