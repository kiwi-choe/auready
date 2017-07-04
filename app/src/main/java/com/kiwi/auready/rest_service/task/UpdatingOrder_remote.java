package com.kiwi.auready.rest_service.task;

import com.google.gson.annotations.SerializedName;

/**
 * Updating taskHead order Model for Remote
 */

public class UpdatingOrder_remote {
    @SerializedName("taskHeadId")
    private final String taskHeadId;
    @SerializedName("orderNum")
    private final int orderNum;

    public UpdatingOrder_remote(String taskHeadId, int orderNum) {
        this.taskHeadId = taskHeadId;
        this.orderNum = orderNum;
    }

    public String getTaskHeadId() {
        return taskHeadId;
    }

    public int getOrderNum() {
        return orderNum;
    }
}
