package com.kiwi.auready.data;


import java.util.ArrayList;
import java.util.List;

/*
 * model class for TaskHeadDetailView
 * */
public class TaskHeadDetail {
    private final TaskHead mTaskHead;
    private final List<Member> mMembers;
    private List<Task> mTasks;
    /*
    * Use this constructor to create a new TaskHead.
    * mTasks is empty
    * */
    public TaskHeadDetail(TaskHead taskHead, List<Member> members) {
        mTaskHead = taskHead;
        mMembers = members;
        mTasks = new ArrayList<>();
    }

    /*
    * when getting from Remote
    * */
    public TaskHeadDetail(TaskHead taskHead, List<Member> members, List<Task> tasks) {
        mTaskHead = taskHead;
        mMembers = members;
        mTasks = tasks;
    }

    public TaskHead getTaskHead() {
        return mTaskHead;
    }
    public List<Member> getMembers() {
        return mMembers;
    }

    public boolean isEmpty() {
        return (mTaskHead.getTitle() == null || "".equals(mTaskHead.getTitle()));
    }

    public List<Task> getTasks() {
        return mTasks;
    }
}
