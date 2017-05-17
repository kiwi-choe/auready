package com.kiwi.auready_ver2.data.local;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.data.Notification;
import com.kiwi.auready_ver2.data.source.NotificationDataSource;
import com.kiwi.auready_ver2.data.source.local.NotificationLocalDataSource;
import com.kiwi.auready_ver2.data.source.local.SQLiteDBHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.List;

import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.DBExceptionTag.INSERT_ERROR;
import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.NotificationEntry.COLUMN_FROM_USERID;
import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.NotificationEntry.COLUMN_FROM_USERNAME;
import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.NotificationEntry.COLUMN_ID;
import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.NotificationEntry.COLUMN_MESSAGE;
import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.NotificationEntry.COLUMN_TYPE;
import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.NotificationEntry.COLUMN_iSNEW;
import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.NotificationEntry.TABLE_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.verify;

/**
 * Notification Local db test
 */
@RunWith(RobolectricTestRunner.class)
public class NotificationLocalDataSourceTest {

    private static String fromUserId0 = "A id";
    private static String fromUserName0 = "A";
    private static String fromUserId1 = "B id";
    private static String fromUserName1 = "B";

    private static final Notification NOTIFICATION = new Notification(Notification.TYPES.friend_request.name(), fromUserId0, fromUserName0, "친구요청");
    private static final List<Notification> NOTIFICATIONS = Lists.newArrayList(
            new Notification(Notification.TYPES.friend_request.name(), fromUserId0, fromUserName0, "친구요청"),
            new Notification(Notification.TYPES.friend_request.name(), fromUserId1, fromUserName1, "친구요청")
    );

    private static SQLiteDBHelper mDbHelper;
    private NotificationLocalDataSource mLocalDataSource = NotificationLocalDataSource.getInstance(RuntimeEnvironment.application);

    @Mock
    private NotificationDataSource.SaveCallback mSaveCallback;

    @Before
    public void setup() {

        MockitoAnnotations.initMocks(this);
        mDbHelper = SQLiteDBHelper.getInstance(RuntimeEnvironment.application);
    }

    @Test
    public void saveNotification_returnsSuccessCallback() {
        mLocalDataSource.saveNotification(NOTIFICATION, mSaveCallback);
        verify(mSaveCallback).onSaveSuccess();
    }

    @Test
    public void saveNotification_queryTest() {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TYPE, NOTIFICATION.getType());
        values.put(COLUMN_iSNEW, NOTIFICATION.getIsNewInteger());
        values.put(COLUMN_FROM_USERID, NOTIFICATION.getFromUserId());
        values.put(COLUMN_FROM_USERNAME, NOTIFICATION.getFromUserName());
        values.put(COLUMN_MESSAGE, NOTIFICATION.getMessage());

        long isSuccessToInsert = mDbHelper.insert(TABLE_NAME, null, values);
        assertTrue(isSuccessToInsert != INSERT_ERROR);
    }

    @Test
    public void retrieveSavedNotification_query() {

        // Save a subbed notification
        mLocalDataSource.saveNotification(NOTIFICATION, mSaveCallback);

        String selection = COLUMN_ID + " LIKE?";
        String[] selectionArgs = {String.valueOf(NOTIFICATION.getId())};
        Cursor c = mDbHelper.query(TABLE_NAME, null, selection, selectionArgs, null, null, null);
        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                int id = c.getInt(c.getColumnIndexOrThrow(COLUMN_ID));
                int type = c.getType(c.getColumnIndexOrThrow(COLUMN_TYPE));
                int isNew = c.getInt(c.getColumnIndexOrThrow(COLUMN_iSNEW));
                String fromUserId = c.getString(c.getColumnIndexOrThrow(COLUMN_FROM_USERID));
                String fromUserName = c.getString(c.getColumnIndexOrThrow(COLUMN_FROM_USERNAME));

                assertNotNull(id);
                assertEquals(type, NOTIFICATION.getType());
                assertEquals(isNew, NOTIFICATION.getIsNewInteger());
                assertEquals(fromUserId, NOTIFICATION.getFromUserId());
                assertEquals(fromUserName, NOTIFICATION.getFromUserName());
            }
        }
    }

    @Test
    public void loadNotifications_checkResultValues() {
        saveStubbedNotifications(NOTIFICATIONS);

        mLocalDataSource.loadNotifications(new NotificationDataSource.LoadNotificationsCallback() {
            @Override
            public void onLoaded(List<Notification> notifications) {
                assertNotNull(notifications);
                assertTrue(notifications.size() == 2);

                // Check result values
                for (Notification notification : notifications) {
                    if (notification.getFromUserId().equals(NOTIFICATIONS.get(0).getFromUserId())) {
                        assertEquals(notification.getType(), NOTIFICATIONS.get(0).getType());
                    }
                    if (notification.getFromUserId().equals(NOTIFICATIONS.get(1).getFromUserId())) {
                        assertEquals(notification.getType(), NOTIFICATIONS.get(1).getType());
                    }
                }
            }

            @Override
            public void onDataNotAvailable() {
                fail();
            }
        });
    }

    @Test
    public void loadNotifications_firesOnLoaded() {
        saveStubbedNotifications(NOTIFICATIONS);

        NotificationDataSource.LoadNotificationsCallback loadCallback = Mockito.mock(NotificationDataSource.LoadNotificationsCallback.class);
        mLocalDataSource.loadNotifications(loadCallback);
        verify(loadCallback).onLoaded(anyListOf(Notification.class));
    }

    private void saveStubbedNotifications(List<Notification> notifications) {
        for (Notification notification : notifications) {
            saveNotification(notification);
        }
    }

    /*
    * Update notification - isNew field
    * */
    @Test
    public void readNotification() {
        saveNotification(NOTIFICATION);
        final Notification[] original = new Notification[1];
        mLocalDataSource.loadNotifications(new NotificationDataSource.LoadNotificationsCallback() {
            @Override
            public void onLoaded(List<Notification> notifications) {
                if (!notifications.isEmpty()) {
                    original[0] = notifications.get(0);
                }
            }

            @Override
            public void onDataNotAvailable() {
                fail();
            }
        });

        // Update the edited notification
        mLocalDataSource.readNotification(original[0].getId());

        // Retrieve the updated notification - isNew should be false
        NotificationDataSource.LoadNotificationsCallback loadCallback = new NotificationDataSource.LoadNotificationsCallback() {
            @Override
            public void onLoaded(List<Notification> notifications) {
                if (notifications.get(0).getId() == original[0].getId()) {

                    assertFalse(notifications.get(0).isNew());
                }
            }

            @Override
            public void onDataNotAvailable() {
                fail();
            }
        };
        mLocalDataSource.loadNotifications(loadCallback);
    }

    @Test
    public void delete_query() {
        saveNotification(NOTIFICATION);

        // Load a saved notification
        final Notification[] notification = new Notification[1];
        mLocalDataSource.loadNotifications(new NotificationDataSource.LoadNotificationsCallback() {
            @Override
            public void onLoaded(List<Notification> notifications) {
                notification[0] = notifications.get(0);
            }

            @Override
            public void onDataNotAvailable() {

            }
        });

        String selection = COLUMN_ID + " LIKE?";
        String[] selectionArgs = {String.valueOf(notification[0].getId())};
        mDbHelper.delete(TABLE_NAME, selection, selectionArgs);

        // Retrieve notifications - returns 0
        Cursor c = mDbHelper.query(TABLE_NAME, null, null, null, null, null, null);
        assertTrue(c.getCount() == 0);
    }

    @Test
    public void deleteNotification_test() {
        saveNotification(NOTIFICATION);

        // Load a saved notification
        final Notification[] notification = new Notification[1];
        mLocalDataSource.loadNotifications(new NotificationDataSource.LoadNotificationsCallback() {
            @Override
            public void onLoaded(List<Notification> notifications) {
                notification[0] = notifications.get(0);
            }

            @Override
            public void onDataNotAvailable() {

            }
        });

        mLocalDataSource.deleteNotification(notification[0].getId());

        // Retrieve notifications - returns 0
        Cursor c = mDbHelper.query(TABLE_NAME, null, null, null, null, null, null);
        assertTrue(c.getCount() == 0);
    }

    @Test
    public void getNotificationsCount_query() {
        saveStubbedNotifications(NOTIFICATIONS);

        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor c = mDbHelper.rawQuery(query, null);
        if (c != null) {
            assertEquals(NOTIFICATIONS.size(), c.getCount());
        } else {
            fail();
        }
    }

    @Test
    public void getNotificationsCount_firesCallbackWithCountValue() {
        saveStubbedNotifications(NOTIFICATIONS);

        NotificationDataSource.GetCountCallback callback = Mockito.mock(NotificationDataSource.GetCountCallback.class);
        mLocalDataSource.getNotificationsCount(callback);
        verify(callback).onSuccessGetCount(NOTIFICATIONS.size());
    }

    private void saveNotification(Notification notification) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TYPE, notification.getType());
        values.put(COLUMN_iSNEW, notification.getIsNewInteger());
        values.put(COLUMN_FROM_USERID, notification.getFromUserId());
        values.put(COLUMN_FROM_USERNAME, notification.getFromUserName());
        values.put(COLUMN_MESSAGE, notification.getMessage());

        mDbHelper.insert(TABLE_NAME, null, values);
    }

    private void deleteAllNotifications() {
        mDbHelper.delete(TABLE_NAME, null, null);
    }

    @After
    public void tearDown() {
        deleteAllNotifications();
        mDbHelper.close();

        NotificationLocalDataSource.destroyInstance();
    }
}