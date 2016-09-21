package com.kiwi.auready_ver2.data.source.local;

/**
 * The contract used for the db to save tables locally.
 */
public final class PersistenceContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public PersistenceContract() {}

    /* TAG */
    public static abstract class DBExceptionTag {
        public static final String TAG_SQLITE = "SQLiteException: ";

    }
    /* Inner class that defines the table contents */
    public static abstract class FriendEntry {
        public static final String TABLE_NAME = "friend";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_NAME = "name";
    }

    public static abstract class TaskHeadEntry {
        public static final String TABLE_NAME = "taskhead";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_TITLE = "title";
    }

    public static abstract class TaskEntry {
        public static final String TABLE_NAME = "task";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_HEAD_ID = "taskheadid";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_COMPLETED = "completed";
        public static final String COLUMN_ORDER = "sequence";
    }

}
