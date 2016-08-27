package com.kiwi.auready_ver2.data;

import android.support.annotation.NonNull;

import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Immutable model class for a TaskHead
 */
public class TaskHead {

    private final String mId;
    private String mTitle;

    /*
    * Use this constructor to create a new TaskHead.
    * */
    public TaskHead(String title) {
        mId = UUID.randomUUID().toString();
        mTitle = title;
    }

    /*
    * Use this constructor to create a new TaskHead with no Title.
    * */
    public TaskHead() {
        mId = UUID.randomUUID().toString();
        mTitle = "";
    }


    /*
    * Use this constructor to create a Task if the TaskHead already has an id
    * (copy of another task)
    * */
    public TaskHead(@NonNull String id, String title) {
        mId = checkNotNull(id, "id cannot be null");
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getId() {
        return mId;
    }
}
