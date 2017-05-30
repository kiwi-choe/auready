package com.kiwi.auready_ver2.rest_service.task;

import com.google.gson.annotations.SerializedName;

/**
 * Model Orders for remote
 */

public class Order_remote {
    @SerializedName("userId")
    private final String userId;

    @SerializedName("orderNum")
    private final int orderNum;

    public Order_remote(String userId, int orderNum) {
        this.userId = userId;
        this.orderNum = orderNum;
    }

    public String getUserId() {
        return userId;
    }

    public int getOrderNum() {
        return orderNum;
    }
}
