package com.kiwi.auready_ver2.data.local;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.data.Member;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.TaskHeadDetail;
import com.kiwi.auready_ver2.data.source.TaskDataSource;
import com.kiwi.auready_ver2.data.source.local.PersistenceContract.MemberEntry;
import com.kiwi.auready_ver2.data.source.local.PersistenceContract.TaskHeadEntry;
import com.kiwi.auready_ver2.data.source.local.SQLiteDBHelper;
import com.kiwi.auready_ver2.data.source.local.TaskLocalDataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.List;

import static com.kiwi.auready_ver2.StubbedData.TaskStub.TASKHEAD;
import static com.kiwi.auready_ver2.StubbedData.TaskStub.TASKHEAD_DETAIL;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;

/**
 * {@link TaskHead} and {@link Member} Local test
 */

@RunWith(RobolectricTestRunner.class)
public class TaskHeadDetailLocalDataSourceTest {

    private static SQLiteDBHelper mDbHelper;
    private TaskLocalDataSource mLocalDataSource = TaskLocalDataSource.getInstance(RuntimeEnvironment.application);

    @Before
    public void setup() {
        mDbHelper = SQLiteDBHelper.getInstance(RuntimeEnvironment.application);
    }

    @Test
    public void saveMembers_testTheFunction_insertTaskHeadAndMembers() {
        // Save a taskHead
        ContentValues taskHeadValues = new ContentValues();
        taskHeadValues.put(TaskHeadEntry.COLUMN_ID, TASKHEAD.getId());
        taskHeadValues.put(TaskHeadEntry.COLUMN_TITLE, TASKHEAD.getTitle());
        taskHeadValues.put(TaskHeadEntry.COLUMN_ORDER, TASKHEAD.getOrder());

        // Save new members without taskHeadId_fk
        // Coz member of the new taskHeadDetail didnt set taskHeadId
        List<Member> tmpMembers = new ArrayList<>();
        tmpMembers.add(new Member("memberId0", null, "userId", "memberName0", "memberEmail0"));
        tmpMembers.add(new Member("memberId1", null, "userId", "memberName1", "memberEmail1"));
        // Set taskHeadId to the new members
        List<Member> members = new ArrayList<>();
        for (Member member : tmpMembers) {
            members.add(new Member(member.getId(), TASKHEAD.getId(), member.getUserId(), member.getName(), member.getEmail()));
        }

        List<ContentValues> memberValuesList = new ArrayList<>();
        for (Member member : members) {
            ContentValues memberValues = new ContentValues();
            memberValues.put(MemberEntry.COLUMN_ID, member.getId());
            memberValues.put(MemberEntry.COLUMN_HEAD_ID_FK, member.getTaskHeadId());
            memberValues.put(MemberEntry.COLUMN_USER_ID, member.getUserId());
            memberValues.put(MemberEntry.COLUMN_NAME, member.getName());
            memberValuesList.add(memberValues);
        }
        // insert two tables
        mDbHelper.insertTaskHeadAndMembers(taskHeadValues, memberValuesList);

        // Retrieve the saved members using query directly
        String[] projection = {
                MemberEntry.COLUMN_ID,
                MemberEntry.COLUMN_HEAD_ID_FK,
                MemberEntry.COLUMN_NAME
        };
        String selection = MemberEntry.COLUMN_HEAD_ID_FK + " LIKE?";
        String[] selectionArgs = {TASKHEAD.getId()};
        Cursor c = mDbHelper.query(MemberEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String id = c.getString(c.getColumnIndexOrThrow(MemberEntry.COLUMN_ID));
                String taskHeadId_fk = c.getString(c.getColumnIndexOrThrow(MemberEntry.COLUMN_HEAD_ID_FK));
                String name = c.getString(c.getColumnIndexOrThrow(MemberEntry.COLUMN_NAME));

                assertThat(taskHeadId_fk, is(TASKHEAD.getId()));
                if (id.equals(tmpMembers.get(0).getId())) {
                    assertThat(name, is(tmpMembers.get(0).getName()));
                }
                if (id.equals(tmpMembers.get(1).getId())) {
                    assertThat(name, is(tmpMembers.get(1).getName()));
                }
            }

        }
    }

    @Test
    public void getTaskHeadDetail_getMembers() {

        // Save TaskHeadDetail
        saveStubbedTaskHeadDetail(TASKHEAD_DETAIL);

        // 1. get TaskHeadDetail and verify that taskHeadId of members is not null
        // 2. verify that param taskHeadId is same to callback taskHeadId
        TaskDataSource.GetTaskHeadDetailCallback getTaskHeadDetailCallback = new TaskDataSource.GetTaskHeadDetailCallback() {
            @Override
            public void onTaskHeadDetailLoaded(TaskHeadDetail taskHeadDetail) {
                if (taskHeadDetail != null) {
                    assertThat(taskHeadDetail.getTaskHead().getId(), is(TASKHEAD.getId()));
                    List<Member> members = taskHeadDetail.getMembers();
                    if (members != null) {
                        for (Member member : members) {
                            assertNotNull(member.getTaskHeadId());
                            assertThat(member.getTaskHeadId(), is(TASKHEAD.getId()));
                        }
                    }
                }
            }

            @Override
            public void onDataNotAvailable() {
                fail();
            }
        };
        mLocalDataSource.getTaskHeadDetail(TASKHEAD.getId(), getTaskHeadDetailCallback);

        deleteAllTaskHeadDetails();
    }

    @Test
    public void getTaskHeadDetail_getTaskHead() {
        saveStubbedTaskHeadDetail(TASKHEAD_DETAIL);

        final String taskHeadId = TASKHEAD_DETAIL.getTaskHead().getId();
        TaskDataSource.GetTaskHeadDetailCallback getTaskHeadDetailCallback = new TaskDataSource.GetTaskHeadDetailCallback() {
            @Override
            public void onTaskHeadDetailLoaded(TaskHeadDetail taskHeadDetail) {
                assertEquals(TASKHEAD_DETAIL.getTaskHead().getTitle(), taskHeadDetail.getTaskHead().getTitle());
            }

            @Override
            public void onDataNotAvailable() {

            }
        };
        mLocalDataSource.getTaskHeadDetail(taskHeadId, getTaskHeadDetailCallback);
    }

    @Test
    public void saveTaskHeadDetail_returnCallback() {
        TaskDataSource.SaveCallback saveCallback = Mockito.mock(TaskDataSource.SaveCallback.class);
        mLocalDataSource.saveTaskHeadDetail(TASKHEAD_DETAIL, saveCallback);
        verify(saveCallback).onSaveSuccess();

        deleteAllTaskHeadDetails();
    }

    @Test
    public void saveTaskHeadDetail_validateMemberIdIsUnique() {
        TaskDataSource.SaveCallback saveCallback = Mockito.mock(TaskDataSource.SaveCallback.class);
        List<Member> newMembers = Lists.newArrayList(
                new Member(TASKHEAD.getId(), "stubbedFriendId0", "name1", "email1"),
                new Member(TASKHEAD.getId(), "stubbedFriendId0", "name2", "email2"),
                new Member(TASKHEAD.getId(), "stubbedFriendId0", "name3", "email3"));

        TaskHeadDetail newTaskHeadDetail = new TaskHeadDetail(TASKHEAD, newMembers);
        mLocalDataSource.saveTaskHeadDetail(newTaskHeadDetail, saveCallback);
        verify(saveCallback).onSaveSuccess();
        deleteAllTaskHeadDetails();
    }
    @Test
    public void editTaskHeadDetail_updateTaskHead() {
        // Save the stubbed taskHeadDetail
        TaskDataSource.SaveCallback saveCallback = Mockito.mock(TaskDataSource.SaveCallback.class);
        mLocalDataSource.saveTaskHeadDetail(TASKHEAD_DETAIL, saveCallback);
        String taskHeadId = TASKHEAD_DETAIL.getTaskHead().getId();

        final String editTitle = "editTitle";
        TaskHead editTaskHead = new TaskHead(taskHeadId, editTitle, TASKHEAD_DETAIL.getTaskHead().getOrder(), TASKHEAD_DETAIL.getTaskHead().getColor());
        TaskDataSource.EditTaskHeadDetailCallback editCallback = Mockito.mock(TaskDataSource.EditTaskHeadDetailCallback.class);
        mLocalDataSource.editTaskHeadDetail(editTaskHead, new ArrayList<Member>(0), editCallback);
        verify(editCallback).onEditSuccess();

        // Retrieve the updating taskHead
        TaskDataSource.GetTaskHeadDetailCallback getTaskHeadDetailCallback = new TaskDataSource.GetTaskHeadDetailCallback() {
            @Override
            public void onTaskHeadDetailLoaded(TaskHeadDetail taskHeadDetail) {
                assertEquals(editTitle, taskHeadDetail.getTaskHead().getTitle());
            }

            @Override
            public void onDataNotAvailable() {

            }
        };
        mLocalDataSource.getTaskHeadDetail(taskHeadId, getTaskHeadDetailCallback);
    }

    @Test
    public void editTaskHeadDetail_addMembers() {

        // Save the stubbed taskHeadDetail
        TaskDataSource.SaveCallback saveCallback = Mockito.mock(TaskDataSource.SaveCallback.class);
        mLocalDataSource.saveTaskHeadDetail(TASKHEAD_DETAIL, saveCallback);
        String taskHeadId = TASKHEAD_DETAIL.getTaskHead().getId();

        TaskDataSource.EditTaskHeadDetailCallback editCallback = Mockito.mock(TaskDataSource.EditTaskHeadDetailCallback.class);
        // Add 2 members
        List<Member> addingMembers = new ArrayList<>(0);
        addingMembers.add(new Member("addingMemberId1", taskHeadId, "userId", "addingMemberName1", "addingMemberEmail1"));
        addingMembers.add(new Member("addingMemberId2", taskHeadId, "userId", "addingMemberName2", "addingMemberEmail2"));
        mLocalDataSource.editTaskHeadDetail(TASKHEAD_DETAIL.getTaskHead(), addingMembers, editCallback);
        verify(editCallback).onEditSuccess();

        // Retrieve the updating taskHead
        TaskDataSource.GetTaskHeadDetailCallback getTaskHeadDetailCallback = new TaskDataSource.GetTaskHeadDetailCallback() {
            @Override
            public void onTaskHeadDetailLoaded(TaskHeadDetail taskHeadDetail) {
                if (taskHeadDetail.getMembers() != null) {
                    List<Member> members = taskHeadDetail.getMembers();
                    assertThat(members.size(), is(5));
                    for (Member member : members) {
                        if (member.getId().equals("addingMemberId1")) {
                            assertThat("addingMemberName1", is(member.getName()));
                        }
                        if (member.getId().equals("addingMemberId2")) {
                            assertEquals("addingMemberName2", member.getName());
                        }
                    }
                }
            }

            @Override
            public void onDataNotAvailable() {
                fail();
            }
        };
        mLocalDataSource.getTaskHeadDetail(taskHeadId, getTaskHeadDetailCallback);
    }

    private void saveStubbedTaskHeadDetail(TaskHeadDetail taskHeadDetail) {

        // Save TaskHead
        TaskHead taskHead = taskHeadDetail.getTaskHead();
        String taskHeadId = taskHead.getId();
        ContentValues taskHeadValues = new ContentValues();
        taskHeadValues.put(TaskHeadEntry.COLUMN_ID, taskHeadId);
        taskHeadValues.put(TaskHeadEntry.COLUMN_TITLE, taskHead.getTitle());
        taskHeadValues.put(TaskHeadEntry.COLUMN_ORDER, taskHead.getOrder());

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
            memberValues.put(MemberEntry.COLUMN_ID, member.getId());
            memberValues.put(MemberEntry.COLUMN_HEAD_ID_FK, member.getTaskHeadId());
            memberValues.put(MemberEntry.COLUMN_USER_ID, member.getUserId());
            memberValues.put(MemberEntry.COLUMN_NAME, member.getName());
            memberValuesList.add(memberValues);
        }
        // insert two tables
        mDbHelper.insertTaskHeadAndMembers(taskHeadValues, memberValuesList);
    }

    private void deleteAllTaskHeadDetails() {
        mDbHelper.delete(TaskHeadEntry.TABLE_NAME, null, null);
    }

    @After
    public void tearDown() {
        mDbHelper.close();
    }


}
