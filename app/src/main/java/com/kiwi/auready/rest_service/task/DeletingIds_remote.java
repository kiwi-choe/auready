package com.kiwi.auready.rest_service.task;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Deleting taskhead ids model for remote
 */

public class DeletingIds_remote {
    @SerializedName("ids")
    private final List<String> ids;

    public DeletingIds_remote(List<String> ids) {
        this.ids = ids;
    }

    public List<String> getIds() {
        return ids;
    }
}
