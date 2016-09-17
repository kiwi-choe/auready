package com.kiwi.auready_ver2.util;

import com.kiwi.auready_ver2.data.Task;

import java.util.Comparator;
import java.util.List;

/**
 * Created by kiwi on 9/12/16.
 */
public class OrderAscCompare implements Comparator<Task> {

    @Override
    public int compare(Task task1, Task task2) {
        return (task1.getOrder() < task2.getOrder())?-1:
                (task1.getOrder() > task2.getOrder())?1:0;
    }
}
