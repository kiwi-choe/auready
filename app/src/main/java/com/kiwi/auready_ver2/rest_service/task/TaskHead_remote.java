package com.kiwi.auready_ver2.rest_service.task;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Model for remote API
 */

public class TaskHead_remote {
    @SerializedName("id")
    private final String id;
    @SerializedName("title")
    private final String title;
    @SerializedName("members")
    private final List<Member_remote> members;

    public TaskHead_remote(String id, String title, List<Member_remote> members) {
        this.id = id;
        this.title = title;
        this.members = members;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
