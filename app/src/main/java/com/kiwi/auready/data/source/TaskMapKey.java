package com.kiwi.auready.data.source;

/**
 * Created by kiwi on 12/13/16.
 */
public class TaskMapKey {

    private final String taskHeadId;
    private final String memberId;

    public TaskMapKey(String taskHeadId, String memberId) {
        this.taskHeadId = taskHeadId;
        this.memberId = memberId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if(o == null) return false;
        if (!(o instanceof TaskMapKey)) return false;

        TaskMapKey key = (TaskMapKey) o;
        if(key.taskHeadId == null || key.memberId == null) return false;
        return taskHeadId == key.taskHeadId && memberId == key.memberId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((taskHeadId == null) ? 0 : taskHeadId.hashCode());
        result = prime * result + ((memberId == null) ? 0 : memberId.hashCode());
        return result;
    }
}
