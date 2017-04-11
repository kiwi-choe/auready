package com.kiwi.auready_ver2.taskheads.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.data.source.TaskDataSource;
import com.kiwi.auready_ver2.data.source.TaskRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Deletes all data in Local
 */
public class InitializeLocalData extends UseCase<InitializeLocalData.RequestValues, InitializeLocalData.ResponseValue> {

    private final TaskRepository mRepository;

    public InitializeLocalData(@NonNull TaskRepository taskRepository) {
        mRepository = checkNotNull(taskRepository);
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        mRepository.initializeLocalData(new TaskDataSource.InitLocalDataCallback() {
            @Override
            public void onInitSuccess() {
                getUseCaseCallback().onSuccess(new ResponseValue());
            }

            @Override
            public void onInitFail() {
                getUseCaseCallback().onError();
            }
        });

        getUseCaseCallback().onSuccess(new ResponseValue());
    }

    public static final class RequestValues implements UseCase.RequestValues {
    }

    public static final class ResponseValue implements UseCase.ResponseValue {
    }
}
