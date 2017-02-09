package com.kiwi.auready_ver2.data;

/**
 * Searched user in FindView
 */

public class SearchedUser {

    public static final int NO_STATUS = -1;
    public static final int PENDING = 0;

    private String name;
    private int status;     // -1: no status, 0: pending

    public SearchedUser(String name, int status) {
        this.name = name;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public int getStatus() {
        return status;
    }
}
