package com.kiwi.auready_ver2;

/**
 * Use cases are the entry points to the domain layer.
 *
 * @param <Q> the request type
 * @param <P> the response type
 */
public abstract class UseCase<Q extends  UseCase.RequestValues, P extends UseCase.ResponseValue> {

    private Q mRequestValues;

    private UseCaseCallback<P> mUseCaseCallback;

    public void setRequestValues(Q requestValues) {
        mRequestValues = requestValues;
    }
    public Q getRequsetValues() {
        return mRequestValues;
    }

    public void setUseCaseCallback(UseCaseCallback<P> useCaseCallback) {
        mUseCaseCallback = useCaseCallback;
    }
    public UseCaseCallback<P> getUseCaseCallback() {
        return mUseCaseCallback;
    }

    void run() {
        executeUseCase(mRequestValues);
    }

    protected abstract void executeUseCase(Q requestValues);

    /*
    * Data passed to a request
    * */
    public interface RequestValues {

    }

    /*
    * Data received from a request.
    * */
    public interface ResponseValue {

    }

    public interface UseCaseCallback<R> {
        void onSuccess(R response);
        void onError();
    }
}
