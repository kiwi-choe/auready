package com.kiwi.auready.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.UUID;

/**
 * model class for DB
 */
public class Member {
    private final String mId;   // unique id: it's not the same with taskHeadId or userId
    private final String mTaskHeadId;
    private final String mUserId;
    private final String mName;
    private final String mEmail;

    /*
    * 1. When Creating a new taskhead
    * 2. edit the taskhead
    * */
    public Member(@Nullable String taskHeadId, @NonNull String userId, @NonNull String name, @NonNull String email) {
        mId = UUID.randomUUID().toString();
        mTaskHeadId = taskHeadId;
        mUserId = userId;
        mName = name;
        mEmail = email;
    }

    public Member(@NonNull String id, @NonNull String taskHeadId, String userId, String name, String email) {
        mId = id;
        mTaskHeadId = taskHeadId;
        mUserId = userId;
        mName = name;
        mEmail = email;
    }

    public String getName() {
        return mName;
    }

    public String getId() {
        return mId;
    }

    public String getTaskHeadId() {
        return mTaskHeadId;
    }

    public String getUserId() {
        return mUserId;
    }

    public String getEmail() {
        return mEmail;
    }

    @Override
    public String toString() {
        return "id: " + mId + " taskHeadId: " + mTaskHeadId +
                " userId: " + mUserId + " name: " + mName + " email: " + mEmail;
    }
}
