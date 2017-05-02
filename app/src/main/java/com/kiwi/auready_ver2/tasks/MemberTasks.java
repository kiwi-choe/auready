package com.kiwi.auready_ver2.tasks;

import com.google.gson.annotations.SerializedName;
import com.kiwi.auready_ver2.rest_service.task.Task_remote;

import java.util.List;

/**
 * tasks of a member
 * Member id, tasks
 */

public class MemberTasks {
    @SerializedName("memberid")
    private final String mMemberId;
    @SerializedName("tasks")
    private final List<Task_remote> mTasks;

    public MemberTasks(String memberId, List<Task_remote> tasks) {
        mMemberId = memberId;
        mTasks = tasks;
    }

    public List<Task_remote> getTasks() {
        return mTasks;
    }

    public String getMemberId() {
        return mMemberId;
    }
}
