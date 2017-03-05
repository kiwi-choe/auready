package com.kiwi.auready_ver2.tasks;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.taskheaddetail.TaskHeadDetailFragment;
import com.kiwi.auready_ver2.tasks.domain.usecase.DeleteTasks;
import com.kiwi.auready_ver2.tasks.domain.usecase.EditTasks;
import com.kiwi.auready_ver2.tasks.domain.usecase.GetMembers;
import com.kiwi.auready_ver2.tasks.domain.usecase.GetTasksOfMember;
import com.kiwi.auready_ver2.tasks.domain.usecase.GetTasksOfTaskHead;
import com.kiwi.auready_ver2.tasks.domain.usecase.SaveTask;

import java.util.LinkedList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by kiwi on 8/26/16.
 */
public class TasksPresenter implements TasksContract.Presenter {

    private final UseCaseHandler mUseCaseHandler;
    private final TasksContract.View mTasksView;

    private String mTaskHeadId;

    /*
    * Task List that can be controlled - add, delete, modify
    * For TasksAdapter and TaskRepository
    * */
    public LinkedList<Task> mTaskList;
    private final GetMembers mGetMembers;
    private final GetTasksOfMember mGetTasksOfMember;
    private final SaveTask mSaveTask;
    private final DeleteTasks mDeleteTasks;
    private final EditTasks mEditTasks;
    private final GetTasksOfTaskHead mGetTasksOfTaskHead;

    public TasksPresenter(@NonNull UseCaseHandler useCaseHandler,
                          String taskHeadId,
                          @NonNull TasksContract.View tasksView,
                          @NonNull GetMembers getMembers,
                          @NonNull GetTasksOfMember getTasksOfMember,
                          @NonNull SaveTask saveTask,
                          @NonNull DeleteTasks deleteTasks,
                          @NonNull EditTasks editTasks,
                          @NonNull GetTasksOfTaskHead getTasksOfTaskHead) {
        mUseCaseHandler = checkNotNull(useCaseHandler, "usecaseHandler cannot be null");
        mTaskHeadId = taskHeadId;
        mTasksView = checkNotNull(tasksView, "tasksView cannot be null!");
        mGetMembers = checkNotNull(getMembers);
        mGetTasksOfMember = checkNotNull(getTasksOfMember);
        mSaveTask = checkNotNull(saveTask);
        mDeleteTasks = checkNotNull(deleteTasks);
        mEditTasks = checkNotNull(editTasks);
        mGetTasksOfTaskHead = checkNotNull(getTasksOfTaskHead);

        mTasksView.setPresenter(this);
        // init mTaskList
        mTaskList = new LinkedList<>();
    }

    @Override
    public void start() {
        if (mTaskHeadId != null) {
            populateMembers();
        }
    }

    @Override
    public void populateMembers() {
        mUseCaseHandler.execute(mGetMembers, new GetMembers.RequestValues(mTaskHeadId),
                new UseCase.UseCaseCallback<GetMembers.ResponseValue>() {

                    @Override
                    public void onSuccess(GetMembers.ResponseValue response) {
                        mTasksView.showMembers(response.getMembers());
                    }

                    @Override
                    public void onError() {
                    }
                });
    }

    @Override
    public void getTasksOfMember(@NonNull final String memberId) {
        checkNotNull(memberId);

        mUseCaseHandler.execute(mGetTasksOfMember, new GetTasksOfMember.RequestValues(memberId),
                new UseCase.UseCaseCallback<GetTasksOfMember.ResponseValue>() {
                    @Override
                    public void onSuccess(GetTasksOfMember.ResponseValue response) {
                        if(response.getTasks().size() != 0) {
                            mTasksView.showTasks(memberId, response.getTasks());
                        }
                    }

                    @Override
                    public void onError() {
                    }
                });
    }

    @Override
    public void createTask(@NonNull final String memberId, @NonNull String description, @NonNull int order) {
        Task newTask = new Task(memberId, description, order);
        mUseCaseHandler.execute(mSaveTask, new SaveTask.RequestValues(newTask),
                new UseCase.UseCaseCallback<SaveTask.ResponseValue>() {

                    @Override
                    public void onSuccess(SaveTask.ResponseValue response) {
                        getTasksOfMember(memberId);
//                        mTasksView.scrollToAddButton();
                    }

                    @Override
                    public void onError() {

                    }
                });
    }


    @Override
    public void result(int requestCode, int resultCode, Intent data) {
        if (TasksActivity.REQ_EDIT_TASKHEAD == requestCode && Activity.RESULT_OK == resultCode) {
            if (data.hasExtra(TaskHeadDetailFragment.EXTRA_TITLE)) {
                String title = data.getStringExtra(TaskHeadDetailFragment.EXTRA_TITLE);
                mTasksView.setTitle(title);
            }
        }
    }

    @Override
    public void deleteTasks(@NonNull final String memberId, @NonNull List<String> taskIds) {
        checkNotNull(taskIds);

        mUseCaseHandler.execute(mDeleteTasks, new DeleteTasks.RequestValues(taskIds),
                new UseCase.UseCaseCallback<DeleteTasks.ResponseValue>() {

                    @Override
                    public void onSuccess(DeleteTasks.ResponseValue response) {
                        getTasksOfMember(memberId);
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    @Override
    public void editTasks(@NonNull final String memberId, @NonNull List<Task> tasks) {

        mUseCaseHandler.execute(mEditTasks, new EditTasks.RequestValues(tasks),
                new UseCase.UseCaseCallback<EditTasks.ResponseValue>() {

                    @Override
                    public void onSuccess(EditTasks.ResponseValue response) {
                        getTasksOfMember(memberId);
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    @Override
    public void filterTasks(List<Task> tasks, List<Task> completed, List<Task> uncompleted) {
        for(Task task:tasks) {
            if(task.getCompleted()) {
                completed.add(task);
            } else {
                uncompleted.add(task);
            }
        }
        mTasksView.showFilteredTasks(completed, uncompleted);
    }
}
