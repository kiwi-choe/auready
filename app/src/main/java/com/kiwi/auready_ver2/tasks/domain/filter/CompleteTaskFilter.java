package com.kiwi.auready_ver2.tasks.domain.filter;

import com.kiwi.auready_ver2.data.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kiwi on 9/1/16.
 */
public class CompleteTaskFilter implements FilterFactory.TaskFilter {
    @Override
    public List<Task> filter(List<Task> tasks) {
        List<Task> filteredTasks = new ArrayList<>();
        for(Task task:tasks) {
            if(task.isCompleted()) {
                filteredTasks.add(task);
            }
        }
        return  filteredTasks;
    }
}
