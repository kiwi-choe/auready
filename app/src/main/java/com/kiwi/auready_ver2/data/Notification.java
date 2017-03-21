package com.kiwi.auready_ver2.data;

/**
 * Notification model for Local
 */

public class Notification {

    // String key
    public static final String TYPE = "fcm_msg_type";
    public static final String CONTENTS = "contents";

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
}
