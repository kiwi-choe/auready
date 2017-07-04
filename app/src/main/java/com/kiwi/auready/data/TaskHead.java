package com.kiwi.auready.data;

import android.support.annotation.NonNull;

import java.util.UUID;

/**
 * Immutable model class for DB
 */
public class TaskHead {

    private final String mId;
    private final String mTitle;
    private final int mOrder;
    private final int mColor;

    /*
    * to create a new TaskHead
    * */
    public TaskHead(String title, int order, int color) {
        mId = UUID.randomUUID().toString();
        mTitle = title;
        mOrder = order;
        mColor = color;
    }

    /*
    * to update TaskHead
    * */
    public TaskHead(@NonNull String id, String title, int order, int color) {
        mId = id;
        mTitle = title;
        mOrder = order;
        mColor = color;
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

    public int getColor() {
        return mColor;
    }
}
