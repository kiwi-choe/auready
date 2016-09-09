package com.kiwi.auready_ver2.data;

import android.support.annotation.NonNull;

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
    * To create a new active Task
    * */
    public Task(@NonNull String taskHeadId) {
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
    public Task(String taskHeadId, String id, String description) {
        mTaskHeadId = taskHeadId;
        mId = id;
        mDescription = description;
        mCompleted = false;
    }

    /*
    * To create a completed Task.
    *
    * A completed task can be made after creating an active task
    * */
    public Task(String taskHeadId, String id, String description, boolean completed) {
        mTaskHeadId = taskHeadId;
        mId = id;
        mDescription = description;
        mCompleted = completed;
    }

    /*
    * To create a completed Task.
    * Use this only on testing.
    * */
    public Task(String taskHeadId, String description, boolean completed) {
        mTaskHeadId = taskHeadId;
        mId = UUID.randomUUID().toString();;
        mDescription = description;
        mCompleted = completed;
    }

    public String getDescription() {
        return mDescription;
    }

    public boolean isCompleted() {
        return mCompleted;
    }

    public boolean isActive() {
        return !mCompleted;
    }

    public String getId() {
        return mId;
    }

    public String getTaskHeadId() {
        return mTaskHeadId;
    }

}
