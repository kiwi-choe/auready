package com.kiwi.auready_ver2.tasks.domain.filter;

import com.kiwi.auready_ver2.data.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Returns all the tasks from a list of {@link Task}s.
 */
public class FilterAllTaskFilter implements FilterFactory.TaskFilter {

    @Override
    public List<Task> filter(List<Task> tasks) {

        return new ArrayList<>(tasks);
    }
}
