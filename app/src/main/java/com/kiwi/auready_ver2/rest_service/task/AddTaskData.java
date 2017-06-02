package com.kiwi.auready_ver2.rest_service.task;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Add task; new task, editing tasks
 */

public class AddTaskData {
    @SerializedName("newTask")
    private final Task_remote newTask;
    @SerializedName("editingTasks")
    private final List<Task_remote> editingTasks;

    public AddTaskData(Task_remote newTask, List<Task_remote> editingTasks) {
        this.newTask = newTask;
        this.editingTasks = editingTasks;
    }
}
