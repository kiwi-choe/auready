package com.kiwi.auready;

/**
 * A scheduler that executes synchronously, for testing purposes.
 */
public class TestUseCaseScheduler implements UseCaseScheduler {
    @Override
    public void execute(Runnable runnable) {
        runnable.run();
    }

    @Override
    public <V extends UseCase.ResponseValue> void notifyResponse(V response, UseCase.UseCaseCallback<V> useCaseCallback) {
        useCaseCallback.onSuccess(response);
    }

    @Override
    public <V extends UseCase.ResponseValue> void onError(UseCase.UseCaseCallback<V> useCaseCallback) {
        useCaseCallback.onError();
    }
}
