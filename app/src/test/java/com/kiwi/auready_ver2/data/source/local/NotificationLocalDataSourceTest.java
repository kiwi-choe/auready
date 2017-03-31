package com.kiwi.auready_ver2.data.source.local;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.data.Notification;

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
import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.NotificationEntry.COLUMN_CONTENTS;
import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.NotificationEntry.COLUMN_ID;
import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.NotificationEntry.COLUMN_TYPE;
import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.NotificationEntry.COLUMN_iSNEW;
import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.NotificationEntry.TABLE_NAME;
import static org.junit.Assert.assertEquals;
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

    private static final Notification NOTIFICATION = new Notification(Notification.TYPES.friend_request.name(), "userA requests friending to userB.");
    private static final List<Notification> NOTIFICATIONS = Lists.newArrayList(
            new Notification(Notification.TYPES.friend_request.name(), "userA requests friending to userB"),
            new Notification(Notification.TYPES.invite_new_member.name(), "userB invite userA")
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

        deleteAllNotifications();
    }

    @Test
    public void saveNotification_queryTest() {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TYPE, NOTIFICATION.getType());
        values.put(COLUMN_iSNEW, NOTIFICATION.isNew());
        values.put(COLUMN_CONTENTS, NOTIFICATION.getContents());

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
                String contents = c.getString(c.getColumnIndexOrThrow(COLUMN_CONTENTS));
                
                assertNotNull(id);
                assertEquals(type, NOTIFICATION.getType());
                assertEquals((isNew > 0), NOTIFICATION.isNew());
                assertEquals(contents, NOTIFICATION.getContents());
            }
        }

        deleteAllNotifications();
    }

    @Test
    public void loadNotifications_checkResultValues() {
        saveStubbedNotifications(NOTIFICATIONS);

        mLocalDataSource.loadNotifications(new NotificationDataSource.LoadNotificationsCallback() {
            @Override
            public void onLoaded(List<Notification> notifications) {
                assertNotNull(notifications);
                assertTrue(notifications.size() == 2);

                assertEquals(NOTIFICATIONS.get(0).getId(), notifications.get(0).getId());
                assertEquals(NOTIFICATIONS.get(1).getId(), notifications.get(1).getId());
            }

            @Override
            public void onDataNotAvailable() {
                fail();
            }
        });

        deleteAllNotifications();
    }

    @Test
    public void loadNotifications_firesOnLoaded() {
        saveStubbedNotifications(NOTIFICATIONS);

        NotificationDataSource.LoadNotificationsCallback loadCallback = Mockito.mock(NotificationDataSource.LoadNotificationsCallback.class);
        mLocalDataSource.loadNotifications(loadCallback);
        verify(loadCallback).onLoaded(anyListOf(Notification.class));

        deleteAllNotifications();
    }
    private void saveStubbedNotifications(List<Notification> notifications) {
        for (Notification notification : notifications) {
            saveNotification(notification);
        }
    }

    private void saveNotification(Notification notification) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TYPE, notification.getType());
        values.put(COLUMN_iSNEW, notification.isNew());
        values.put(COLUMN_CONTENTS, notification.getContents());

        mDbHelper.insert(TABLE_NAME, null, values);
    }

    private void deleteAllNotifications() {
        mDbHelper.delete(TABLE_NAME, null, null);
    }
}