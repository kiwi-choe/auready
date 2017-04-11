package com.kiwi.auready_ver2.data;

/**
 * Notification model for Local
 */

public class Notification {

    // String key
    public static final String TYPE = "noti_type";

    public static final String FROM_USERID = "fromUserId";
    public static final String FROM_USERNAME = "fromUserName";

    public int getId() {
        return mId;
    }

    public int getType() {
        return mType;
    }

    public boolean isNew() {
        return mIsNew;
    }

    public int getIsNewInteger() {
        return (mIsNew ? 1 : 0);
    }

    public String getFromUserId() {
        return mFromUserId;
    }

    public String getFromUserName() {
        return mFromUserName;
    }

    /*
    * type;
    * 1. Friend request
    * 2. Response of Friend request
    * 3. Inviting new members to TaskHead
    * */
    public enum TYPES {
        friend_request(0), res_friend_request(1), invite_new_member(2);

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
    private final String mFromUserId;
    private final String mFromUserName;

    // Save new notification
    public Notification(String type, String fromUserId, String fromUserName) {
        // mId is an autoincrement value
        mType = TYPES.valueOf(type).getIntType();
        mFromUserId = fromUserId;
        mFromUserName = fromUserName;
        mIsNew = true;
    }

    /*
    * Get from Local db
    * */
    public Notification(int id, int type, int isNew, String fromUserId, String fromUserName) {
        mId = id;
        mType = type;
        mIsNew = (isNew > 0);

        mFromUserId = fromUserId;
        mFromUserName = fromUserName;
    }
    @Override
    public String toString() {
        return "id: " + mId + " type: " + mType +
                " isNew: " + mIsNew + " fromUserId: " + mFromUserId +
                " fromUserName: " + mFromUserName;
    }
}
