package com.kiwi.auready.data;

import com.google.gson.annotations.SerializedName;

/**
 * Searched user in FindView
 */

public class SearchedUser {

    public static final int NO_STATUS = 2;
    public static final int PENDING = 0;

    @SerializedName("userInfo")
    private User userInfo;
    @SerializedName("status")
    private int status;     // 2: no status, 0: pending

    public SearchedUser(User userInfo, int status) {
        this.userInfo = userInfo;
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public User getUserInfo() {
        return userInfo;
    }
}
