package com.kiwi.auready_ver2.tasks.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.data.source.TaskDataSource;
import com.kiwi.auready_ver2.data.source.TaskRepository;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Fetches the list of tasks by taskHeadId.
 */
public class GetTasks extends UseCase<GetTasks.RequestValues, GetTasks.ResponseValue> {


    private final TaskRepository mTaskRepository;

    public GetTasks(@NonNull TaskRepository taskRepository) {
        mTaskRepository = checkNotNull(taskRepository, "taskRepository cannot be null");
    }

    @Override
    protected void executeUseCase(final RequestValues values) {

        mTaskRepository.getTasksByTaskHeadId(values.getTaskHeadId(), new TaskDataSource.GetTasksCallback() {

            @Override
            public void onTasksLoaded(List<Task> tasks) {
                ResponseValue responseValue = new ResponseValue(tasks);
                getUseCaseCallback().onSuccess(responseValue);
            }

            @Override
            public void onDataNotAvailable() {
                getUseCaseCallback().onError();
            }
        });

    }

    public static class RequestValues implements UseCase.RequestValues {
        private final String mTaskHeadId;

        public RequestValues(@NonNull String taskHeadId) {
            mTaskHeadId = checkNotNull(taskHeadId);
        }

        public String getTaskHeadId() {
            return mTaskHeadId;
        }
    }

    public class ResponseValue implements UseCase.ResponseValue {

        private final List<Task> mTasks;


        public ResponseValue(List<Task> tasks) {
            mTasks = tasks;
        }

        public List<Task> getTasks() {
            return mTasks;
        }
    }
}
