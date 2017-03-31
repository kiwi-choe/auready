package com.kiwi.auready_ver2.data;

/**
 * Notification model for Local
 */

public class Notification {

    // String key
    public static final String TYPE = "fcm_msg_type";
    public static final String CONTENTS = "contents";

    public int getId() {
        return mId;
    }

    public int getType() {
        return mType;
    }

    public boolean isNew() {
        return mIsNew;
    }

    public String getContents() {
        return mContents;
    }

    /*
    * type;
    * 1. Friend request
    * 2. Response of Friend request
    * 3. Inviting new members to TaskHead
    * */
    public enum TYPES {
        friend_request(1), res_friend_request(2), invite_new_member(3);

        private final int intType;

        TYPES(int intType) {
            this.intType = intType;
        }

        public int getIntType() {
            return intType;
        }
    }

    private int mId;
    private final int mType;
    private final boolean mIsNew;
    private final String mContents;

    // Save new notification
    public Notification(String type, String contents) {
        // mId is an autoincrement value
        mType = TYPES.valueOf(type).getIntType();
        mContents = contents;
        mIsNew = true;
    }

    /*
    * Get from Local db
    * */
    public Notification(int id, int type, int isNew, String contents) {
        mId = id;
        mType = type;
        mIsNew = (isNew > 0);
        mContents = contents;
    }
}
