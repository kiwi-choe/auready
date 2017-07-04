package com.kiwi.auready.rest_service.task;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Model Member for remote
 */

public class Member_remote {
    @SerializedName("id")
    private final String id;
    @SerializedName("userId")
    private final String userId;
    @SerializedName("name")
    private final String name;
    @SerializedName("email")
    private final String email;

    @SerializedName("tasks")
    private final List<Task_remote> tasks;

    public Member_remote(String id, String userId, String name, String email, List<Task_remote> tasks) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.tasks = tasks;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getUserId() {
        return userId;
    }

    public List<Task_remote> getTasks() {
        return tasks;
    }
}
