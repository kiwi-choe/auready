package com.kiwi.auready_ver2.data.source.remote;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.source.TaskHeadDataSource;

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
    public void editTitle(@NonNull TaskHead taskHead) {

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
    public void deleteTaskHead(@NonNull String id) {

        TASKHEADS_SERVICE_DATA.remove(id);
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
