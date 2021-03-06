package com.kiwi.auready.tasks.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready.UseCase;
import com.kiwi.auready.data.Task;
import com.kiwi.auready.data.source.TaskDataSource;
import com.kiwi.auready.data.source.TaskRepository;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Fetches the list of tasks by taskHeadId.
 */
public class GetTasksOfTaskHead extends UseCase<GetTasksOfTaskHead.RequestValues, GetTasksOfTaskHead.ResponseValue> {


    private final TaskRepository mTaskRepository;

    public GetTasksOfTaskHead(@NonNull TaskRepository taskRepository) {
        mTaskRepository = checkNotNull(taskRepository, "taskRepository cannot be null");
    }

    @Override
    protected void executeUseCase(final RequestValues values) {
        mTaskRepository.getTasksOfTaskHead(values.getTaskHeadId(), new TaskDataSource.LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                getUseCaseCallback().onSuccess(new ResponseValue(tasks));
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
