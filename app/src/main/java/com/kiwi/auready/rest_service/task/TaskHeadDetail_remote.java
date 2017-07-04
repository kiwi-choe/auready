package com.kiwi.auready.rest_service.task;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Model for remote API
 */

public class TaskHeadDetail_remote {
    @SerializedName("id")
    private final String id;
    @SerializedName("title")
    private final String title;
    @SerializedName("color")
    private final int color;
    @SerializedName("orders")
    private final List<Order_remote> orders;
    @SerializedName("members")
    private final List<Member_remote> members;

    public TaskHeadDetail_remote(String id, String title, int color, List<Order_remote> orders, List<Member_remote> members) {
        this.id = id;
        this.title = title;
        this.color = color;
        this.orders = orders;
        this.members = members;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getColor() {
        return color;
    }

    public List<Member_remote> getMembers() {
        return members;
    }

    public List<Order_remote> getOrders() {
        return orders;
    }
}
