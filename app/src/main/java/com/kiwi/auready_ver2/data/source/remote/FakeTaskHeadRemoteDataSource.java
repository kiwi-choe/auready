package com.kiwi.auready_ver2.data.source.remote;

import android.support.annotation.NonNull;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.source.TaskHeadDataSource;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by kiwi on 8/23/16.
 */
public class FakeTaskHeadRemoteDataSource implements TaskHeadDataSource {

    private static final Map<String, TaskHead> TASKHEADS_SERVICE_DATA = new LinkedHashMap<>();

    private static FakeTaskHeadRemoteDataSource INSTANCE;

    public static FakeTaskHeadRemoteDataSource getInstance() {
        return INSTANCE;
    }

    @Override
    public void getTaskHeads(@NonNull LoadTaskHeadsCallback callback) {
        callback.onTaskHeadsLoaded(Lists.newArrayList(TASKHEADS_SERVICE_DATA.values()));
    }
}
