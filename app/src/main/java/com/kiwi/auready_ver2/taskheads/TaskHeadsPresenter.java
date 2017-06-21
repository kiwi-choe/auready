package com.kiwi.auready_ver2.taskheads;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.TaskHeadDetail;
import com.kiwi.auready_ver2.settings.SettingsFragment;
import com.kiwi.auready_ver2.taskheaddetail.TaskHeadDetailFragment;
import com.kiwi.auready_ver2.taskheads.domain.usecase.DeleteTaskHeads;
import com.kiwi.auready_ver2.taskheads.domain.usecase.GetTaskHeadDetails;
import com.kiwi.auready_ver2.taskheads.domain.usecase.GetTaskHeadsCount;
import com.kiwi.auready_ver2.taskheads.domain.usecase.UpdateTaskHeadOrders;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TaskHead presenter
 */
public class TaskHeadsPresenter implements TaskHeadsContract.Presenter {

    private static final int DEFAULT_COLOR = R.color.color_picker_default_color;

    private final TaskHeadsContract.View mTaskHeadView;

    private UseCaseHandler mUseCaseHandler;
    private final GetTaskHeadDetails mGetTaskHeadDetails;
    private final DeleteTaskHeads mDeleteTaskHeads;
    private final GetTaskHeadsCount mGetTaskHeadCount;
    private UpdateTaskHeadOrders mUpdateTaskHeadOrders;

    public TaskHeadsPresenter(UseCaseHandler useCaseHandler, @NonNull TaskHeadsContract.View tasksView,
                              @NonNull GetTaskHeadDetails getTaskHeadDetails,
                              @NonNull DeleteTaskHeads deleteTaskHeads,
                              @NonNull GetTaskHeadsCount getTaskHeadCount,
                              @NonNull UpdateTaskHeadOrders updateTaskHeadOrders) {
        mUseCaseHandler = checkNotNull(useCaseHandler, "useCaseHandler cannot be null");
        mTaskHeadView = checkNotNull(tasksView, "tasksView cannot be null!");
        mGetTaskHeadDetails = checkNotNull(getTaskHeadDetails, "getTaskHeadDetails cannot be null");
        mDeleteTaskHeads = checkNotNull(deleteTaskHeads, "deleteTaskHeads cannot be null");
        mGetTaskHeadCount = checkNotNull(getTaskHeadCount);
        mUpdateTaskHeadOrders = updateTaskHeadOrders;

        mTaskHeadView.setPresenter(this);
    }

    @Override
    public void start() {
        loadTaskHeads(false);
    }

    @Override
    public void loadTaskHeads(boolean forceToUpdate) {

        // Load taskHeadDetail and filter taskheads
        mUseCaseHandler.execute(mGetTaskHeadDetails, new GetTaskHeadDetails.RequestValues(forceToUpdate),
                new UseCase.UseCaseCallback<GetTaskHeadDetails.ResponseValue>() {

                    @Override
                    public void onSuccess(GetTaskHeadDetails.ResponseValue response) {
                        List<TaskHead> taskHeads = filterTaskHeads(response.getTaskHeadDetails());
                        for (TaskHead taskHead : taskHeads) {
                            Log.d("Tag_loadTaskHeads", taskHead.getTitle() + " - " + String.valueOf(taskHead.getOrder()));
                        }
                        processTaskHeads(taskHeads);
                    }

                    @Override
                    public void onError() {
                        mTaskHeadView.showNoTaskHeads();
                    }
                });
    }

    private List<TaskHead> filterTaskHeads(List<TaskHeadDetail> taskHeadDetails) {
        List<TaskHead> taskHeads = new ArrayList<>();
        for (TaskHeadDetail taskHeadDetail : taskHeadDetails) {
            taskHeads.add(taskHeadDetail.getTaskHead());
        }
        return taskHeads;
    }

    private void processTaskHeads(List<TaskHead> taskHeads) {
        if (taskHeads.isEmpty()) {
            mTaskHeadView.showNoTaskHeads();
        } else {
            mTaskHeadView.showTaskHeads(taskHeads);
        }
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
            int color = DEFAULT_COLOR;
            if (data.hasExtra(TaskHeadDetailFragment.EXTRA_COLOR)) {
                color = data.getIntExtra(TaskHeadDetailFragment.EXTRA_COLOR, DEFAULT_COLOR);
            }

            mTaskHeadView.showTasksView(taskHeadId, title, color);

            // Canceled create taskhead, Open TaskHeadsView
        }
        if (TaskHeadsActivity.REQ_SETTINGS == requestCode && Activity.RESULT_OK == resultCode) {
            boolean isSuccess = data.getBooleanExtra(SettingsFragment.EXTRA_LOGOUT, false);
            if (isSuccess) {
                mTaskHeadView.showAccountView();
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
                        Log.d("Tag_delete_presenter", "success");
                        loadTaskHeads(false);
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
        for (int i = 0; i < size; i++) {
            TaskHead taskHead = taskheads.get(i);
            updatingTaskHeads.add(new TaskHead(taskHead.getId(), taskHead.getTitle(), i, taskHead.getColor()));
        }

        mUseCaseHandler.execute(mUpdateTaskHeadOrders, new UpdateTaskHeadOrders.RequestValues(updatingTaskHeads),
                new UseCase.UseCaseCallback<UpdateTaskHeadOrders.ResponseValue>() {

                    @Override
                    public void onSuccess(UpdateTaskHeadOrders.ResponseValue response) {
                        loadTaskHeads(false);
                    }

                    @Override
                    public void onError() {

                    }
                });
    }
}
