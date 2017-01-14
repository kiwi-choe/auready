package com.kiwi.auready_ver2.data;

import android.support.annotation.NonNull;

import java.util.UUID;

/**
 * Created by kiwi on 8/19/16.
 */
public class Task {

    private String mId;

    private String mMemberId;

    private String mDescription;

    private boolean mCompleted;

    private int mOrder;

    /*
    * To create a new active Task
    * with order
    * */
    public Task(@NonNull String memberId,
                String description, int order) {
        mId = UUID.randomUUID().toString();
        mMemberId = memberId;
        mDescription = description;
        mCompleted = false;
        mOrder = order;
    }

    /*
    * To create an active Task if the Task already has an id
    * Update the existing task.
    * */
    public Task(@NonNull String id, @NonNull String memberId,
                String description, int order) {
        mId = id;
        mMemberId = memberId;
        mDescription = description;
        mCompleted = false;
        mOrder = order;
    }

    /*
    * To create a completed Task.
    * Use this only on testing.
    * */
    public Task(@NonNull String memberId,
                String description, boolean completed, int order) {
        mId = UUID.randomUUID().toString();
        mMemberId = memberId;
        mDescription = description;
        mCompleted = completed;
        mOrder = order;
    }

    /*
    * Get task from local db
    * */
    public Task(String id, String memberId,
                String description, boolean completed, int order) {
        mId = id;
        mMemberId = memberId;
        mDescription = description;
        mCompleted = completed;
        mOrder = order;
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

    public int getOrder() {
        return mOrder;
    }
    public void setOrder(int order) {
        mOrder = order;
    }

    public void increaseOrder() {
        mOrder++;
    }

    public void decreaseOrder() {
        mOrder--;
    }

    public boolean getCompleted() {
        return mCompleted;
    }

    public String getMemberId() {
        return mMemberId;
    }

    public boolean isEmpty() {
        return (mDescription == null || "".equals(mDescription));
    }
}
