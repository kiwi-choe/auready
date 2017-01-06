package com.kiwi.auready_ver2.data.source;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.data.Member;
import com.kiwi.auready_ver2.data.TaskHead;

import java.util.List;

/**
 * Fake RemoteDataSource of TaskHeadDetail
 */
public class FakeTaskHeadDetailRemoteDataSource implements TaskHeadDetailDataSource {

    private static FakeTaskHeadDetailRemoteDataSource INSTANCE;

    public static FakeTaskHeadDetailRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FakeTaskHeadDetailRemoteDataSource();
        }
        return INSTANCE;
    }

    @Override
    public void deleteTaskHeadDetail(String taskHeadId) {

    }

    @Override
    public void getTaskHeadDetail(@NonNull String taskHeadId, @NonNull GetTaskHeadDetailCallback callback) {

    }

    @Override
    public void saveTaskHead(@NonNull TaskHead taskHead, @NonNull SaveCallback callback) {

    }

    @Override
    public void saveMembers(@NonNull List<Member> members, @NonNull SaveCallback callback) {

    }
}