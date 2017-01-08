package com.kiwi.auready_ver2.data;

import android.support.annotation.NonNull;

import java.util.UUID;

/**
 * model class for DB
 */
public class Member {
    private final String mId;   // unique id: it's not the same with taskHeadId or friendId
    private final String mTaskHeadId;
    private final String mFriendId;
    private final String mName;

    /*
    * when Create new taskHead
    * */
    public Member(String friendId, String name) {
        mId = UUID.randomUUID().toString();
        mTaskHeadId = null;
        mFriendId = friendId;
        mName = name;
    }

    public Member(String taskHeadId, String friendId, String name) {
        mId = UUID.randomUUID().toString();
        mTaskHeadId = taskHeadId;
        mFriendId = friendId;
        mName = name;
    }

    public Member(@NonNull String id, String taskHeadId, String friendId, String name) {
        mId = id;
        mTaskHeadId = taskHeadId;
        mFriendId = friendId;
        mName = name;
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

    public String getFriendId() {
        return mFriendId;
    }

    @Override
    public String toString() {
        return "id: " + mId + " taskHeadId: " + mTaskHeadId +
                " friendId: " + mFriendId + " name: " + mName;

    }
}
