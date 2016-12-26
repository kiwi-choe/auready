package com.kiwi.auready_ver2;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.data.TaskHead;

import java.util.List;

/**
 * Stubbed data for test
 */
public final class StubbedData {

    // To prevent someone from accidentally instantiating this class
    // give it an empty constructor.
    public StubbedData() {
    }

    /*
    * {@link Task}s stub that is added to the fake service API layer.
    * */
    public static abstract class TaskStub {

        public static final List<Friend> MEMBERS = Lists.newArrayList(new Friend("email1", "name1"), new Friend("email2", "name2"),
                new Friend("email3", "name3"));
        private static final int ORDER = 0;
        public static TaskHead TASKHEAD = new TaskHead("title1", MEMBERS, ORDER);
        // 3 tasks, one active and two completed of MEMBER the index 0
        public static List<Task> TASKS = Lists.newArrayList(
                new Task(TASKHEAD.getId(), MEMBERS.get(0).getId(), "description", 0),
                new Task(TASKHEAD.getId(), MEMBERS.get(0).getId(), "description2", true, 0),
                new Task(TASKHEAD.getId(), MEMBERS.get(0).getId(), "description3", true, 0));
    }

}
