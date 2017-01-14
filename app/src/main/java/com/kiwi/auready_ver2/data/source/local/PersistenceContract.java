package com.kiwi.auready_ver2.data.source.local;

/**
 * The contract used for the db to save tables locally.
 */
public final class PersistenceContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public PersistenceContract() {}

    /* TAG */
    static class DBExceptionTag {
        static final String TAG_SQLITE = "SQLiteException: ";

        static final long INSERT_ERROR = -1;
        public static final long INSERT_NOTHING = 0;
        public static final int DELETE_NOTHING = 0;
    }
    /* Inner class that defines the table contents */
    public static class FriendEntry {
        public static final String TABLE_NAME = "friend";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_NAME = "name";
    }

    public static class MemberEntry {
        public static final String TABLE_NAME = "member";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_HEAD_ID_FK = "taskheadid";
        public static final String COLUMN_FRIEND_ID_FK = "friendid";
        public static final String COLUMN_NAME = "name";
    }

    public static class TaskHeadEntry {
        public static final String TABLE_NAME = "taskhead";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_ORDER = "sequence";
    }

    public static class TaskEntry {
        public static final String TABLE_NAME = "task";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_MEMBER_ID_FK = "memberid";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_COMPLETED = "completed";
        public static final String COLUMN_ORDER = "sequence";
    }

    static class SQL_CREATE_TABLE {
        // Database info
        // If you change the db scheme, you must increment the database version
        static final int DATABASE_VERSION = 1;
        static final String DATABASE_NAME = "Auready.db";

        private static final String PRIMARY_KEY = " PRIMARY KEY, ";
        private static final String FOREIGN_KEY = "FOREIGN KEY(";
        private static final String REFERENCES = ") REFERENCES ";
        private static final String TEXT_TYPE = " TEXT";
        private static final String INTEGER_TYPE = " INTEGER";
        private static final String COMMA_SEP = ", ";
        private static final String ON_DELETE_CASCADE = " ON DELETE CASCADE";

        // insert, update, delete, execSQL, ...

        static final String SQL_CREATE_FRIEND_TABLE =
                "CREATE TABLE IF NOT EXISTS " + FriendEntry.TABLE_NAME + " (" +
                        FriendEntry.COLUMN_ID + TEXT_TYPE + PRIMARY_KEY +
                        FriendEntry.COLUMN_EMAIL + TEXT_TYPE + COMMA_SEP +
                        FriendEntry.COLUMN_NAME + TEXT_TYPE +
                        " )";

        static final String SQL_CREATE_TASKHEAD_TABLE =
                "CREATE TABLE IF NOT EXISTS " + TaskHeadEntry.TABLE_NAME + " (" +
                        TaskHeadEntry.COLUMN_ID + TEXT_TYPE + PRIMARY_KEY +
                        TaskHeadEntry.COLUMN_TITLE + TEXT_TYPE + COMMA_SEP +
                        TaskHeadEntry.COLUMN_ORDER + INTEGER_TYPE +
                        " )";

        static final String SQL_CREATE_MEMBER_TABLE =
                "CREATE TABLE IF NOT EXISTS " + MemberEntry.TABLE_NAME + " (" +
                        MemberEntry.COLUMN_ID + TEXT_TYPE + PRIMARY_KEY +
                        MemberEntry.COLUMN_HEAD_ID_FK + TEXT_TYPE + COMMA_SEP +
                        MemberEntry.COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                        MemberEntry.COLUMN_FRIEND_ID_FK + TEXT_TYPE + COMMA_SEP +
                        FOREIGN_KEY + MemberEntry.COLUMN_HEAD_ID_FK +
                        REFERENCES + TaskHeadEntry.TABLE_NAME + "(" + TaskHeadEntry.COLUMN_ID + ")" + COMMA_SEP +
                        FOREIGN_KEY + MemberEntry.COLUMN_FRIEND_ID_FK +
                        REFERENCES + FriendEntry.TABLE_NAME + "(" + FriendEntry.COLUMN_ID + ")" +
                        ON_DELETE_CASCADE +
                        " )";

        static final String SQL_CREATE_TASK_TABLE =
                "CREATE TABLE IF NOT EXISTS " + TaskEntry.TABLE_NAME + " (" +
                        TaskEntry.COLUMN_ID + TEXT_TYPE + PRIMARY_KEY +
                        TaskEntry.COLUMN_MEMBER_ID_FK + TEXT_TYPE + COMMA_SEP +
                        TaskEntry.COLUMN_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                        TaskEntry.COLUMN_COMPLETED + INTEGER_TYPE + COMMA_SEP +
                        TaskEntry.COLUMN_ORDER + INTEGER_TYPE + COMMA_SEP +
                        FOREIGN_KEY + TaskEntry.COLUMN_MEMBER_ID_FK +
                        REFERENCES + MemberEntry.TABLE_NAME + "(" + MemberEntry.COLUMN_ID + ")" +
                        ON_DELETE_CASCADE +
                        " )";

    }
}
