package com.kiwi.auready_ver2.tasks.domain.filter;

import com.kiwi.auready_ver2.data.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Returns the active tasks from a list of {@link com.kiwi.auready_ver2.data.Task}s.
 */
public class ActiveTaskFilter implements FilterFactory.TaskFilter {
    @Override
    public List<Task> filter(List<Task> tasks) {
        List<Task> filteredTasks = new ArrayList<>();
        for(Task task:tasks) {
            if(task.isActive()) {
                filteredTasks.add(task);
            }
        }
        return filteredTasks;
    }
}
