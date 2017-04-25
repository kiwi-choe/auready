package com.kiwi.auready_ver2.rest_service.task;

import com.google.gson.annotations.SerializedName;

/**
 * Task model for remote
 */

public class Task_remote {
    @SerializedName("id")
    private final String mId;
    @SerializedName("description")
    private final String mDescription;
    @SerializedName("completed")
    private final boolean mCompleted;
    @SerializedName("order")
    private final int mOrder;

    public Task_remote(String mId, String mDescription, boolean mCompleted, int mOrder) {
        this.mId = mId;
        this.mDescription = mDescription;
        this.mCompleted = mCompleted;
        this.mOrder = mOrder;
    }

    public String getId() {
        return mId;
    }

    public String getDescription() {
        return mDescription;
    }

    public boolean getCompleted() {
        return mCompleted;
    }

    public int getOrder() {
        return mOrder;
    }
}
