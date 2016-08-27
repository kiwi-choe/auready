package com.kiwi.auready_ver2.data;

import java.util.UUID;

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
    * Use this constructor to create a Task if the TaskHead already has an id
    * (copy of another task)
    * */
    public TaskHead(String id, String title) {
        mId = id;
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getId() {
        return mId;
    }
}
