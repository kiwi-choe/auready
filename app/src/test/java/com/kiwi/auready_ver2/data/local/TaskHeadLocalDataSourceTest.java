package com.kiwi.auready_ver2.data.local;

import android.content.ContentValues;
import android.database.Cursor;

import com.kiwi.auready_ver2.StubbedData;
import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.Member;
import com.kiwi.auready_ver2.data.Notification;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.TaskHeadDetail;
import com.kiwi.auready_ver2.data.source.TaskDataSource;
import com.kiwi.auready_ver2.data.source.local.PersistenceContract;
import com.kiwi.auready_ver2.data.source.local.SQLiteDBHelper;
import com.kiwi.auready_ver2.data.source.local.TaskLocalDataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.List;

import static com.kiwi.auready_ver2.StubbedData.TaskStub.TASKHEADS;
import static com.kiwi.auready_ver2.StubbedData.TaskStub.TASKHEAD_DETAIL;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;

/**
 * TaskHead Local DataSource test
 */

@RunWith(RobolectricTestRunner.class)
public class TaskHeadLocalDataSourceTest {

    // notification stub data
    private static String fromUserId0 = "A id";
    private static String fromUserName0 = "A";
    private static final Notification NOTIFICATION = new Notification(Notification.TYPES.friend_request.name(), fromUserId0, fromUserName0);

    private static SQLiteDBHelper mDbHelper;
    private TaskLocalDataSource mTaskLocalDataSource = TaskLocalDataSource.getInstance(RuntimeEnvironment.application);

    @Captor
    private ArgumentCaptor<TaskDataSource.LoadTaskHeadsCallback> mLoadTaskHeadsCallbackCaptor;

    @Before
    public void setup() {
        mDbHelper = SQLiteDBHelper.getInstance(RuntimeEnvironment.application);
    }

    @Test
    public void getTaskHeads() {

        saveStubbedTaskHeads(TASKHEADS);

        TaskDataSource.LoadTaskHeadsCallback loadTaskHeadsCallback = new TaskDataSource.LoadTaskHeadsCallback() {
            @Override
            public void onTaskHeadsLoaded(List<TaskHead> taskHeads) {
                assertEquals(TASKHEADS.get(0).getId(), taskHeads.get(0).getId());
                assertEquals(TASKHEADS.get(0).getTitle(), taskHeads.get(0).getTitle());

                assertEquals(TASKHEADS.get(1).getId(), taskHeads.get(1).getId());
                assertEquals(TASKHEADS.get(1).getTitle(), taskHeads.get(1).getTitle());
            }

            @Override
            public void onDataNotAvailable() {
                fail();
            }
        };

        mTaskLocalDataSource.getTaskHeads(loadTaskHeadsCallback);

        deleteStubbedTaskHead();
    }

    @Test
    public void getTaskHeads_failed_whenSaveFailed() {
        // save failed
        TaskDataSource.LoadTaskHeadsCallback loadTaskHeadsCallback = Mockito.mock(TaskDataSource.LoadTaskHeadsCallback.class);
        mTaskLocalDataSource.getTaskHeads(loadTaskHeadsCallback);

        verify(loadTaskHeadsCallback).onDataNotAvailable();
    }

    @Test
    public void getTaskHeadsCount() {
        // Save 3 taskHeads
        saveStubbedTaskHeads(TASKHEADS);

        // Verify that returned taskHeadsCount is 3
        int actualTaskHeadsCount = mTaskLocalDataSource.getTaskHeadsCount();
        assertThat(actualTaskHeadsCount, is(3));

        deleteStubbedTaskHead();
    }

    @Test
    public void deleteTaskHeads_retrieveExistingTaskHeads() {
        saveStubbedTaskHeads(TASKHEADS);

        // Delete taskHeads - index 0, 2
        List<String> deletingTaskHeadIds = new ArrayList<>();
        deletingTaskHeadIds.add(TASKHEADS.get(0).getId());
        deletingTaskHeadIds.add(TASKHEADS.get(2).getId());
        mTaskLocalDataSource.deleteTaskHeads(deletingTaskHeadIds);

        // Verify if taskHeads are deleted
        TaskDataSource.LoadTaskHeadsCallback loadTaskHeadsCallback = new TaskDataSource.LoadTaskHeadsCallback() {
            @Override
            public void onTaskHeadsLoaded(List<TaskHead> taskHeads) {
                assertEquals(TASKHEADS.get(1).getId(), taskHeads.get(0).getId());
                assertEquals(TASKHEADS.get(1).getTitle(), taskHeads.get(0).getTitle());

                assertThat(taskHeads.size(), is(1));
            }

            @Override
            public void onDataNotAvailable() {
                fail();
            }
        };
        mTaskLocalDataSource.getTaskHeads(loadTaskHeadsCallback);

        deleteStubbedTaskHead();
    }

    @Test
    public void updateTaskHeadOrders_retrieveUpdatingTaskHeads() {
        saveStubbedTaskHeads(TASKHEADS);

        TaskHead taskHead0 = TASKHEADS.get(0);
        TaskHead taskHead1 = TASKHEADS.get(1);

        List<TaskHead> updatingTaskHeads = new ArrayList<>();
        final TaskHead updating0 = new TaskHead(taskHead0.getId(), taskHead0.getTitle(), 100, taskHead0.getColor());
        updatingTaskHeads.add(updating0);
        final TaskHead updating1 = new TaskHead(taskHead1.getId(), taskHead1.getTitle(), 200, taskHead1.getColor());
        updatingTaskHeads.add(updating1);
        mTaskLocalDataSource.updateTaskHeadOrders(updatingTaskHeads);

        // Verify if taskHeads are updating
        TaskDataSource.LoadTaskHeadsCallback loadTaskHeadsCallback = new TaskDataSource.LoadTaskHeadsCallback() {
            @Override
            public void onTaskHeadsLoaded(List<TaskHead> taskHeads) {
                for(TaskHead taskHead: taskHeads) {
                    if(taskHead.getId().equals(updating0.getId())) {

                        assertEquals(100, taskHead.getOrder());
                    }
                    if(taskHead.getId().equals(updating1.getId())) {
                        assertEquals(200, taskHead.getOrder());
                    }
                }
            }

            @Override
            public void onDataNotAvailable() {
                fail();
            }
        };
        mTaskLocalDataSource.getTaskHeads(loadTaskHeadsCallback);

        deleteStubbedTaskHead();
    }

    /*
    * Initialize All data in Local Database
    * */

    @Test
    public void initializeLocalData_queryTest() {
        saveStubbedTaskHeadDetail(TASKHEAD_DETAIL);
        saveStubbedFriend(StubbedData.FriendStub.FRIENDS.get(0));
        saveStubbedNotification(NOTIFICATION);

        mDbHelper.delete(PersistenceContract.TaskHeadEntry.TABLE_NAME, null, null);
        // cascade
//        mDbHelper.delete(PersistenceContract.MemberEntry.TABLE_NAME, null, null);
//        mDbHelper.delete(PersistenceContract.TaskEntry.TABLE_NAME, null, null);
        mDbHelper.delete(PersistenceContract.FriendEntry.TABLE_NAME, null, null);
        mDbHelper.delete(PersistenceContract.NotificationEntry.TABLE_NAME, null, null);

        // Verify that no data in Local db
        List<Friend> friends = retrieveSavedFriends();
        assertEquals(friends.size(), 0);
    }

    @Test
    public void initializeLocalData_methodTest() {
        saveStubbedTaskHeadDetail(TASKHEAD_DETAIL);
        saveStubbedFriend(StubbedData.FriendStub.FRIENDS.get(0));
        saveStubbedNotification(NOTIFICATION);

        TaskDataSource.InitLocalDataCallback initLocalDataCallback = Mockito.mock(TaskDataSource.InitLocalDataCallback.class);
        mTaskLocalDataSource.initializeLocalData(initLocalDataCallback);
        verify(initLocalDataCallback).onInitSuccess();

        // Verify that no data in Local db
        List<Friend> friends = retrieveSavedFriends();
        assertEquals(friends.size(), 0);
    }

    private List<Friend> retrieveSavedFriends() {
        List<Friend> friends = new ArrayList<>(0);

        String[] projection = {
                PersistenceContract.FriendEntry.COLUMN_ID,
                PersistenceContract.FriendEntry.COLUMN_EMAIL,
                PersistenceContract.FriendEntry.COLUMN_NAME
        };
        Cursor c = mDbHelper.query(PersistenceContract.FriendEntry.TABLE_NAME, projection, null, null, null, null, null);
        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String id = c.getString(c.getColumnIndexOrThrow(PersistenceContract.FriendEntry.COLUMN_ID));
                String email = c.getString(c.getColumnIndexOrThrow(PersistenceContract.FriendEntry.COLUMN_EMAIL));
                String name = c.getString(c.getColumnIndexOrThrow(PersistenceContract.FriendEntry.COLUMN_NAME));

                friends.add(new Friend(id, email, name));
            }
        }

        return friends;
    }

    private void saveStubbedTaskHeadDetail(TaskHeadDetail taskHeadDetail) {

        // Save TaskHead
        TaskHead taskHead = taskHeadDetail.getTaskHead();
        String taskHeadId = taskHead.getId();
        ContentValues taskHeadValues = new ContentValues();
        taskHeadValues.put(PersistenceContract.TaskHeadEntry.COLUMN_ID, taskHeadId);
        taskHeadValues.put(PersistenceContract.TaskHeadEntry.COLUMN_TITLE, taskHead.getTitle());
        taskHeadValues.put(PersistenceContract.TaskHeadEntry.COLUMN_ORDER, taskHead.getOrder());

        // Save members
        List<Member> tmpMembers = taskHeadDetail.getMembers();

        // Coz member of the new taskHeadDetail didnt set taskHeadId
        List<Member> members = new ArrayList<>();
        for (Member member : tmpMembers) {
            members.add(new Member(member.getId(), taskHeadId, member.getFriendId(), member.getName(), member.getEmail()));
        }

        List<ContentValues> memberValuesList = new ArrayList<>();
        for (Member member : members) {
            ContentValues memberValues = new ContentValues();
            memberValues.put(PersistenceContract.MemberEntry.COLUMN_ID, member.getId());
            memberValues.put(PersistenceContract.MemberEntry.COLUMN_HEAD_ID_FK, member.getTaskHeadId());
            memberValues.put(PersistenceContract.MemberEntry.COLUMN_FRIEND_ID_FK, member.getFriendId());
            memberValues.put(PersistenceContract.MemberEntry.COLUMN_NAME, member.getName());
            memberValuesList.add(memberValues);
        }
        // insert two tables
        mDbHelper.insertTaskHeadAndMembers(taskHeadValues, memberValuesList);
    }

    private void saveStubbedFriend(Friend friend) {
        ContentValues values = new ContentValues();
        values.put(PersistenceContract.FriendEntry.COLUMN_ID, friend.getId());
        values.put(PersistenceContract.FriendEntry.COLUMN_EMAIL, friend.getEmail());
        values.put(PersistenceContract.FriendEntry.COLUMN_NAME, friend.getName());

        mDbHelper.insert(PersistenceContract.FriendEntry.TABLE_NAME, null, values);
    }

    private void saveStubbedNotification(Notification notification) {
        ContentValues values = new ContentValues();
        values.put(PersistenceContract.NotificationEntry.COLUMN_TYPE, notification.getType());
        values.put(PersistenceContract.NotificationEntry.COLUMN_iSNEW, notification.getIsNewInteger());
        values.put(PersistenceContract.NotificationEntry.COLUMN_FROM_USERID, notification.getFromUserId());
        values.put(PersistenceContract.NotificationEntry.COLUMN_FROM_USERNAME, notification.getFromUserName());

        mDbHelper.insert(PersistenceContract.NotificationEntry.TABLE_NAME, null, values);
    }

    /*
    * Convenience methods
    * */
    private void saveStubbedTaskHeads(List<TaskHead> taskHeads) {

        // Save the stubbed taskheads
        for (TaskHead taskHead : taskHeads) {

            ContentValues values = new ContentValues();
            values.put(PersistenceContract.TaskHeadEntry.COLUMN_ID, taskHead.getId());
            values.put(PersistenceContract.TaskHeadEntry.COLUMN_TITLE, taskHead.getTitle());
            values.put(PersistenceContract.TaskHeadEntry.COLUMN_ORDER, taskHead.getOrder());
            values.put(PersistenceContract.TaskHeadEntry.COLUMN_COLOR, taskHead.getColor());

            mDbHelper.insert(PersistenceContract.TaskHeadEntry.TABLE_NAME, null, values);
        }
    }

    private void deleteStubbedTaskHead() {
        mDbHelper.delete(PersistenceContract.TaskHeadEntry.TABLE_NAME, null, null);
    }

    @After
    public void tearDown() {
        mDbHelper.close();
    }


}
