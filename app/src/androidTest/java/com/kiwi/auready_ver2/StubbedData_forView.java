package com.kiwi.auready_ver2;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.data.Member;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.data.TaskHead;

import java.util.List;

/**
 * Stubbed data for test
 */
public final class StubbedData_forView {

    // To prevent someone from accidentally instantiating this class
    // give it an empty constructor.
    public StubbedData_forView() { }

    /*
    * This stub that is added to the fake service API layer.
    * */
    public static final TaskHead TASKHEAD = new TaskHead("title1", 0);
    public static List<TaskHead> TASKHEADS = Lists.newArrayList(
            new TaskHead("title1", 0),
            new TaskHead("title2", 1),
            new TaskHead("title3", 2));

    public static final List<Member> MEMBERS = Lists.newArrayList(
            new Member(TASKHEAD.getId(), "email1", "name1"),
            new Member(TASKHEAD.getId(), "email2", "name2"),
            new Member(TASKHEAD.getId(), "email3", "name3"));

    // 3 tasks, one active and two completed of MEMBER the index 0
    public static List<Task> TASKS = Lists.newArrayList(
            new Task(TASKHEAD.getId(), MEMBERS.get(0).getId(), "description", 0),
            new Task(TASKHEAD.getId(), MEMBERS.get(0).getId(), "description2", true, 0),
            new Task(TASKHEAD.getId(), MEMBERS.get(0).getId(), "description3", true, 0));
}
