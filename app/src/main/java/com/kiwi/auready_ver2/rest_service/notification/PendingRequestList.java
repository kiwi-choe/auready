package com.kiwi.auready_ver2.rest_service.notification;

import com.google.gson.annotations.SerializedName;
import com.kiwi.auready_ver2.data.Friend;

import java.util.List;

/**
 * Friend Request Pending List
 */

public class PendingRequestList {
    @SerializedName("fromUsers")
    private List<Friend> fromUsers;

    public PendingRequestList(List<Friend> fromUsers) {
        this.fromUsers = fromUsers;
    }

    public List<Friend> getFromUsers() {
        return fromUsers;
    }
}
