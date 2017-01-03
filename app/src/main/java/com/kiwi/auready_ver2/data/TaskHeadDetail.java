package com.kiwi.auready_ver2.data;


import java.util.List;

/*
 * model class for TaskHeadDetailView
 * */
public class TaskHeadDetail {
    private final TaskHead mTaskHead;
    private List<Member> mMembers;  // member rows

    /*
    * Use this constructor to create a new TaskHead.
    * */
    public TaskHeadDetail(TaskHead taskHead, List<Member> members) {
        mTaskHead = taskHead;
        mMembers = members;
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
}
