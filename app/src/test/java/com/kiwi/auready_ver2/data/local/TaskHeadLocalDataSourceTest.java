package com.kiwi.auready_ver2.data.local;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.common.collect.Lists;
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
import static com.kiwi.auready_ver2.StubbedData.TaskStub.TASKHEAD_DETAILS;
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
    private static final Notification NOTIFICATION = new Notification(Notification.TYPES.friend_request.name(), fromUserId0, fromUserName0, "친구요청");

    private static SQLiteDBHelper mDbHelper;
    private TaskLocalDataSource mTaskLocalDataSource = TaskLocalDataSource.getInstance(RuntimeEnvironment.application);

    @Captor
    private ArgumentCaptor<TaskDataSource.LoadTaskHeadDetailsCallback> mLoadTaskHeadsCallbackCaptor;

    @Before
    public void setup() {
        mDbHelper = SQLiteDBHelper.getInstance(RuntimeEnvironment.application);
    }

    @Test
    public void getTaskHeadDetails() {

        saveStubbedTaskHeadDetails(TASKHEAD_DETAILS);
//
//        TaskDataSource.LoadTaskHeadDetailsCallback loadTaskHeadDetailsCallback = new TaskDataSource.LoadTaskHeadDetailsCallback() {
//            @Override
//            public void onTaskHeadDetailsLoaded(List<TaskHeadDetail> taskHeadDetails) {
//                assertEquals(TASKHEADS.get(0).getUserId(), taskHeadDetails.get(0).getTaskHead().getUserId());
//                assertEquals(TASKHEADS.get(0).getTitle(), taskHeadDetails.get(0).getTaskHead().getTitle());
//
//                assertEquals(TASKHEADS.get(1).getUserId(), taskHeadDetails.get(1).getTaskHead().getUserId());
//                assertEquals(TASKHEADS.get(1).getTitle(), taskHeadDetails.get(1).getTaskHead().getTitle());
//            }
//
//            @Override
//            public void onDataNotAvailable() {
//                fail();
//            }
//        };
//
//        mTaskLocalDataSource.getTaskHeadDetails(loadTaskHeadDetailsCallback);


        String sql = String.format(
                "SELECT * FROM %s, %s WHERE %s.%s = %s.%s",
                PersistenceContract.TaskHeadEntry.TABLE_NAME, PersistenceContract.MemberEntry.TABLE_NAME,
                PersistenceContract.TaskHeadEntry.TABLE_NAME, PersistenceContract.TaskHeadEntry.COLUMN_ID,
                PersistenceContract.MemberEntry.TABLE_NAME, PersistenceContract.MemberEntry.COLUMN_HEAD_ID_FK);

        Cursor cursor = mDbHelper.getReadableDatabase().rawQuery(sql, null);

        List<TaskHeadDetail> taskHeadDetails = new ArrayList<>(0);
        String taskHeadIdOfPreRow = "";
        int i = -1;
        assertEquals(cursor.getCount(), 6);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                // Set members
                String memberId = cursor.getString(cursor.getColumnIndexOrThrow(PersistenceContract.MemberEntry.COLUMN_ID));
                String taskHeadId_fk = cursor.getString(cursor.getColumnIndexOrThrow(PersistenceContract.MemberEntry.COLUMN_HEAD_ID_FK));
                String userId = cursor.getString(cursor.getColumnIndexOrThrow(PersistenceContract.MemberEntry.COLUMN_USER_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(PersistenceContract.MemberEntry.COLUMN_NAME));
                String email = cursor.getString(cursor.getColumnIndexOrThrow(PersistenceContract.MemberEntry.COLUMN_EMAIL));
                Member member = new Member(memberId, taskHeadId_fk, userId, name, email);

                if (!taskHeadId_fk.equals(taskHeadIdOfPreRow)) {
                    i++;
                    // Set TaskHead
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(PersistenceContract.TaskHeadEntry.COLUMN_TITLE));
                    int order = cursor.getInt(cursor.getColumnIndexOrThrow(PersistenceContract.TaskHeadEntry.COLUMN_ORDER));
                    int color = cursor.getInt(cursor.getColumnIndexOrThrow(PersistenceContract.TaskHeadEntry.COLUMN_COLOR));

                    TaskHead taskHead = new TaskHead(taskHeadId_fk, title, order, color);
                    List<Member> members = Lists.newArrayList(member);
                    TaskHeadDetail taskHeadDetail = new TaskHeadDetail(taskHead, members);
                    taskHeadDetails.add(taskHeadDetail);
                } else {
                    taskHeadDetails.get(i).getMembers().add(member);
                }

                taskHeadIdOfPreRow = taskHeadId_fk;
            }
        }

        if (cursor != null) {
            cursor.close();
        }

        // Validate
        assertEquals(taskHeadDetails.size(), 2);

        assertEquals(taskHeadDetails.get(0).getMembers().size(), 3);
//        assertEquals(taskHeadDetails.get(0).getMem);
    }

    @Test
    public void getTaskHeads_failed_whenSaveFailed() {
        // save failed
        TaskDataSource.LoadTaskHeadDetailsCallback loadTaskHeadDetailsCallback = Mockito.mock(TaskDataSource.LoadTaskHeadDetailsCallback.class);
        mTaskLocalDataSource.getTaskHeadDetails(loadTaskHeadDetailsCallback);

        verify(loadTaskHeadDetailsCallback).onDataNotAvailable();
    }

    @Test
    public void getTaskHeadsCount() {
        // Save 3 taskHeads
        saveStubbedTaskHeadDetails(TASKHEAD_DETAILS);

        // Verify that returned taskHeadsCount is 3
        int actualTaskHeadsCount = mTaskLocalDataSource.getTaskHeadsCount();
        assertThat(actualTaskHeadsCount, is(2));
    }

    @Test
    public void deleteTaskHeads_retrieveExistingTaskHeads() {
        saveStubbedTaskHeadDetails(TASKHEAD_DETAILS);

        // Delete taskHeads - index 0, 2
        List<String> deletingTaskHeadIds = new ArrayList<>();
        deletingTaskHeadIds.add(TASKHEADS.get(0).getId());
        deletingTaskHeadIds.add(TASKHEADS.get(2).getId());

        mTaskLocalDataSource.deleteTaskHeads(deletingTaskHeadIds, new TaskDataSource.DeleteTaskHeadsCallback() {
            @Override
            public void onDeleteSuccess() {
                // Verify if taskHeads are deleted
                TaskDataSource.LoadTaskHeadDetailsCallback loadTaskHeadDetailsCallback = new TaskDataSource.LoadTaskHeadDetailsCallback() {
                    @Override
                    public void onTaskHeadDetailsLoaded(List<TaskHeadDetail> taskHeadDetails) {
                        assertEquals(TASKHEADS.get(1).getId(), taskHeadDetails.get(0).getTaskHead().getId());
                        assertEquals(TASKHEADS.get(1).getTitle(), taskHeadDetails.get(0).getTaskHead().getTitle());

                        assertThat(taskHeadDetails.size(), is(1));
                    }

                    @Override
                    public void onDataNotAvailable() {
                        fail();
                    }
                };
                mTaskLocalDataSource.getTaskHeadDetails(loadTaskHeadDetailsCallback);
            }

            @Override
            public void onDeleteFail() {
                fail();
            }
        });
    }

    @Test
    public void updateTaskHeadOrders_retrieveUpdatingTaskHeads() {
        saveStubbedTaskHeadDetails(TASKHEAD_DETAILS);

        TaskHead taskHead0 = TASKHEADS.get(0);
        TaskHead taskHead1 = TASKHEADS.get(1);

        List<TaskHead> updatingTaskHeads = new ArrayList<>();
        final TaskHead updating0 = new TaskHead(taskHead0.getId(), taskHead0.getTitle(), 555, taskHead0.getColor());
        updatingTaskHeads.add(updating0);
        final TaskHead updating1 = new TaskHead(taskHead1.getId(), taskHead1.getTitle(), 200, taskHead1.getColor());
        updatingTaskHeads.add(updating1);
        TaskDataSource.UpdateTaskHeadOrdersCallback callback = Mockito.mock(TaskDataSource.UpdateTaskHeadOrdersCallback.class);
        mTaskLocalDataSource.updateTaskHeadOrders(updatingTaskHeads, callback);

        // Verify if taskHeads are updating
        TaskDataSource.LoadTaskHeadDetailsCallback loadTaskHeadDetailsCallback = new TaskDataSource.LoadTaskHeadDetailsCallback() {
            @Override
            public void onTaskHeadDetailsLoaded(List<TaskHeadDetail> taskHeadDetails) {
                for (TaskHeadDetail taskHeadDetail : taskHeadDetails) {
                    if (taskHeadDetail.getTaskHead().getId().equals(updating0.getId())) {

                        assertEquals(555, taskHeadDetail.getTaskHead().getOrder());
                    }
                    if (taskHeadDetail.getTaskHead().getId().equals(updating1.getId())) {
                        assertEquals(200, taskHeadDetail.getTaskHead().getOrder());
                    }
                }
            }

            @Override
            public void onDataNotAvailable() {
                fail();
            }
        };
        mTaskLocalDataSource.getTaskHeadDetails(loadTaskHeadDetailsCallback);
    }

    @Test
    public void deleteAllTaskHeads_checkResultCountIsZero_invokeSuccessCallback() {
        saveStubbedTaskHeadDetails(TASKHEAD_DETAILS);

        final TaskDataSource.DeleteAllCallback callback = Mockito.mock(TaskDataSource.DeleteAllCallback.class);
        mTaskLocalDataSource.deleteAllTaskHeads(callback);

        TaskDataSource.LoadTaskHeadDetailsCallback loadCallback = new TaskDataSource.LoadTaskHeadDetailsCallback() {
            @Override
            public void onTaskHeadDetailsLoaded(List<TaskHeadDetail> taskHeadDetails) {
                fail();
            }

            @Override
            public void onDataNotAvailable() {

                verify(callback).onDeleteAllSuccess();
            }
        };
        mTaskLocalDataSource.getTaskHeadDetails(loadCallback);
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

        // Verify that cascade is succeeded
        Cursor c = mDbHelper.query(PersistenceContract.MemberEntry.TABLE_NAME, null, null, null, null, null, null);
        assertEquals(c.getCount(), 0);
    }

    @Test
    public void initializeLocalData_methodTest() {
        saveStubbedTaskHeadDetail(TASKHEAD_DETAIL);
        saveStubbedFriend(StubbedData.FriendStub.FRIENDS.get(0));
        saveStubbedNotification(NOTIFICATION);

        // Init Task
        TaskDataSource.InitLocalDataCallback initLocalDataCallback = Mockito.mock(TaskDataSource.InitLocalDataCallback.class);
        mTaskLocalDataSource.initializeLocalData(initLocalDataCallback);
        verify(initLocalDataCallback).onInitSuccess();

        // Verify that no data in Local db
        List<Friend> friends = retrieveSavedFriends();
        assertEquals(friends.size(), 0);
    }

    @Test
    public void deleteMembers_query() {
        saveStubbedTaskHeadDetail(TASKHEAD_DETAIL);

        // delete members by taskHeadId
        String taskHeadId = TASKHEAD_DETAIL.getMembers().get(0).getTaskHeadId();
        String whereClause = PersistenceContract.MemberEntry.COLUMN_HEAD_ID_FK + " LIKE?";
        String[] whereArgs = {taskHeadId};
        mDbHelper.delete(PersistenceContract.MemberEntry.TABLE_NAME, whereClause, whereArgs);

        // Deleted members size by taskHeadId should be 0
        mTaskLocalDataSource.getMembers(taskHeadId, new TaskDataSource.LoadMembersCallback() {
            @Override
            public void onMembersLoaded(List<Member> members) {
                fail();
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }
    /*
    * Convenience methods
    * */
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
            members.add(new Member(member.getId(), taskHeadId, member.getUserId(), member.getName(), member.getEmail()));
        }

        List<ContentValues> memberValuesList = new ArrayList<>();
        for (Member member : members) {
            ContentValues memberValues = new ContentValues();
            memberValues.put(PersistenceContract.MemberEntry.COLUMN_ID, member.getId());
            memberValues.put(PersistenceContract.MemberEntry.COLUMN_HEAD_ID_FK, member.getTaskHeadId());
            memberValues.put(PersistenceContract.MemberEntry.COLUMN_USER_ID, member.getUserId());
            memberValues.put(PersistenceContract.MemberEntry.COLUMN_NAME, member.getName());
            memberValuesList.add(memberValues);
        }
        // insert two tables
        mDbHelper.insertTaskHeadAndMembers(taskHeadValues, memberValuesList);
    }

    private void saveStubbedFriend(Friend friend) {
        ContentValues values = new ContentValues();
        values.put(PersistenceContract.FriendEntry.COLUMN_ID, friend.getUserId());
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

    private void saveStubbedTaskHeadDetails(List<TaskHeadDetail> taskHeadDetails) {
        for (TaskHeadDetail taskHeadDetail : taskHeadDetails) {

            // Save TaskHead
            TaskHead taskHead = taskHeadDetail.getTaskHead();
            String taskHeadId = taskHead.getId();
            ContentValues taskHeadValues = new ContentValues();
            taskHeadValues.put(PersistenceContract.TaskHeadEntry.COLUMN_ID, taskHeadId);
            taskHeadValues.put(PersistenceContract.TaskHeadEntry.COLUMN_TITLE, taskHead.getTitle());
            taskHeadValues.put(PersistenceContract.TaskHeadEntry.COLUMN_ORDER, taskHead.getOrder());
            taskHeadValues.put(PersistenceContract.TaskHeadEntry.COLUMN_COLOR, taskHead.getColor());

            // Save members
            List<Member> tmpMembers = taskHeadDetail.getMembers();

            // Coz member of the new taskHeadDetail didnt set taskHeadId
            List<Member> members = new ArrayList<>();
            for (Member member : tmpMembers) {
                members.add(new Member(member.getId(), taskHeadId, member.getUserId(), member.getName(), member.getEmail()));
            }

            List<ContentValues> memberValuesList = new ArrayList<>();
            for (Member member : members) {
                ContentValues memberValues = new ContentValues();
                memberValues.put(PersistenceContract.MemberEntry.COLUMN_ID, member.getId());
                memberValues.put(PersistenceContract.MemberEntry.COLUMN_HEAD_ID_FK, member.getTaskHeadId());
                memberValues.put(PersistenceContract.MemberEntry.COLUMN_USER_ID, member.getUserId());
                memberValues.put(PersistenceContract.MemberEntry.COLUMN_NAME, member.getName());
                memberValues.put(PersistenceContract.MemberEntry.COLUMN_EMAIL, member.getEmail());
                memberValuesList.add(memberValues);
            }

            // insert two tables
            mDbHelper.insertTaskHeadAndMembers(taskHeadValues, memberValuesList);
        }
    }

    private void deleteStubbedTaskHead() {
        mDbHelper.delete(PersistenceContract.TaskHeadEntry.TABLE_NAME, null, null);
    }

    @After
    public void tearDown() {
        deleteStubbedTaskHead();
        mDbHelper.close();
    }


}
