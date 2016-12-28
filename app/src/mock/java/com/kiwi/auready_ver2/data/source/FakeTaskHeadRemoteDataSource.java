package com.kiwi.auready_ver2.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.TaskHead;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kiwi on 8/23/16.
 */
public class FakeTaskHeadRemoteDataSource implements TaskHeadDataSource {

    private static final Map<String, TaskHead> TASKHEADS_SERVICE_DATA = new LinkedHashMap<>();

    private static FakeTaskHeadRemoteDataSource INSTANCE;

    public static FakeTaskHeadRemoteDataSource getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new FakeTaskHeadRemoteDataSource();
        }
        return INSTANCE;
    }

    @Override
    public int getTaskHeadsCount() {
        return 0;
    }

    @Override
    public void updateTaskHeadsOrder(List<TaskHead> taskHeads) {

    }

    @Override
    public void editTaskHead(String id, String title, List<Friend> members) {

    }

    @Override
    public void deleteTaskHeads(List<String> taskHeadIds) {

    }

    @Override
    public void getTaskHeads(@NonNull LoadTaskHeadsCallback callback) {

        if(TASKHEADS_SERVICE_DATA.size() == 0) {
            callback.onDataNotAvailable();
        } else {
            callback.onTaskHeadsLoaded(Lists.newArrayList(TASKHEADS_SERVICE_DATA.values()));
        }
    }

    @Override
    public void getTaskHead(@NonNull String taskHeadId, @NonNull GetTaskHeadCallback callback) {

    }

    @Override
    public void deleteAllTaskHeads() {

    }

    @Override
    public void saveTaskHead(@NonNull TaskHead taskHead) {

    }

    @VisibleForTesting
    public void addTaskHeads(List<TaskHead> taskHeads) {
        for(TaskHead taskHead:taskHeads) {
            TASKHEADS_SERVICE_DATA.put(taskHead.getId(), taskHead);
        }
    }
}
