package com.kiwi.auready_ver2.data;

import java.util.UUID;

/**
 * Created by kiwi on 8/19/16.
 */
public class Task {

    private final String mId;

    private final String mTaskHeadId;

    private String mDescription;

    private boolean mCompleted;

    /*
    * To create a new active Task with no description
    * */
    public Task(String taskHeadId) {
        mId = UUID.randomUUID().toString();
        mTaskHeadId = taskHeadId;
        mDescription = "";
        mCompleted = false;
    }
    /*
    * To create a new active Task with description
    * */
    public Task(String taskHeadId, String description) {
        mId = UUID.randomUUID().toString();
        mTaskHeadId = taskHeadId;
        mDescription = description;
        mCompleted = false;
    }

    /*
    * To create an active Task if the Task already has an id
    * (copy of another Task).
    * */
    public Task(String id, String taskHeadId, String description) {
        mId = id;
        mTaskHeadId = taskHeadId;
        mDescription = description;
        mCompleted = false;
    }

    /*
    * To create a new completed Task.
    * */
    public Task(String taskHeadId, String description, boolean completed) {
        mId = UUID.randomUUID().toString();
        mTaskHeadId = taskHeadId;
        mDescription = description;
        mCompleted = completed;
    }

    /*
    * To create an active Task if the Task already has an id
    * (copy of another Task).
    * */
    public Task(String id, String taskHeadId, String description, boolean completed) {
        mId = id;
        mTaskHeadId = taskHeadId;
        mDescription = description;
        mCompleted = false;
    }

    public String getDescription() {
        return mDescription;
    }

    public boolean isCompleted() {
        return mCompleted;
    }

    public String getId() {
        return mId;
    }
}
