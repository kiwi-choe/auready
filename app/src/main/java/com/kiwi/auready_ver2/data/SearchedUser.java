package com.kiwi.auready_ver2.data;

/**
 * Searched user in FindView
 */

public class SearchedUser {

    private String name;
    private int status;     // 0: no status, 1: pending

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
