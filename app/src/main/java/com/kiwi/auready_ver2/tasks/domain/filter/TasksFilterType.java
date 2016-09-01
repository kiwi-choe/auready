package com.kiwi.auready_ver2.tasks.domain.filter;

/**
 * Created by kiwi on 8/31/16.
 */
public enum TasksFilterType {

    /*
    * Do not filter tasks.
    * */
    ACTIVE_TASKS,
    /*
    * Filters only the completed tasks.
    * */
    COMPLETED_TASKS,
    /*
    * Filters only the active tasks.
    * */
    ALL_TASKS
}
