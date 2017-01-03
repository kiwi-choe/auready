package com.kiwi.auready_ver2.data;

import android.support.annotation.NonNull;

import java.util.UUID;

/**
 * Immutable model class for DB
 */
public class TaskHead {

    private final String mId;
    private final String mTitle;
    private final int mOrder;

    /*
    * to create a new TaskHead
    * */
    public TaskHead(String title, int order) {
        mId = UUID.randomUUID().toString();
        mTitle = title;
        mOrder = order;
    }

    /*
    * to update TaskHead
    * */
    public TaskHead(@NonNull String id, String title, int order) {
        mId = id;
        mTitle = title;
        mOrder = order;
    }

    public String getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getOrder() {
        return mOrder;
    }
}
