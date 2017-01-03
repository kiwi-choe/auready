package com.kiwi.auready_ver2.data.source;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.data.TaskHeadDetail;

/**
 * Fake RemoteDataSource of TaskHeadDetail
 */
public class FakeTaskHeadDetailRemoteDataSource implements TaskHeadDetailDataSource {

    private static FakeTaskHeadDetailRemoteDataSource INSTANCE;

    public static FakeTaskHeadDetailRemoteDataSource getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new FakeTaskHeadDetailRemoteDataSource();
        }
        return INSTANCE;
    }

    @Override
    public void deleteTaskHeadDetail(String taskHeadId) {

    }

    @Override
    public void saveTaskHeadDetail(@NonNull TaskHeadDetail taskHeadDetail, @NonNull SaveCallback callback) {

    }

    @Override
    public void getTaskHeadDetail(@NonNull String taskHeadId, @NonNull GetTaskHeadDetailCallback callback) {

    }
}
