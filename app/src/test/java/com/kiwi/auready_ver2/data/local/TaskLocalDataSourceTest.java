package com.kiwi.auready_ver2.data.local;

import android.content.ContentValues;
import android.database.Cursor;

import com.kiwi.auready_ver2.data.Member;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.TaskHeadDetail;
import com.kiwi.auready_ver2.data.source.TaskDataSource;
import com.kiwi.auready_ver2.data.source.local.PersistenceContract;
import com.kiwi.auready_ver2.data.source.local.PersistenceContract.TaskEntry;
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

import static com.kiwi.auready_ver2.StubbedData.TaskStub.MEMBERS;
import static com.kiwi.auready_ver2.StubbedData.TaskStub.TASK;
import static com.kiwi.auready_ver2.StubbedData.TaskStub.TASKHEAD;
import static com.kiwi.auready_ver2.StubbedData.TaskStub.TASKHEAD_DETAIL;
import static com.kiwi.auready_ver2.StubbedData.TaskStub.TASKS;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * {@link Task} Local test
 */

@RunWith(RobolectricTestRunner.class)
public class TaskLocalDataSourceTest {

    private static SQLiteDBHelper mDbHelper;
    private TaskLocalDataSource mLocalDataSource = TaskLocalDataSource.getInstance(RuntimeEnvironment.application);
    @Captor
    private ArgumentCaptor<TaskDataSource.LoadTasksCallback> mLoadTasksCallbackCaptor;

    @Before
    public void setup() {
        mDbHelper = SQLiteDBHelper.getInstance(RuntimeEnvironment.application);
    }

    @Test
    public void saveTask() {

        mLocalDataSource.saveTask(TASK);

        // Retrieve the saved task using query directly
        String selection = TaskEntry.COLUMN_ID + " LIKE?";
        String[] selectionArgs = {TASK.getId()};
        Cursor c = mDbHelper.query(TaskEntry.TABLE_NAME, null, selection, selectionArgs, null, null, null);
        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String id = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_ID));
                String description = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_DESCRIPTION));
                boolean completed = c.getInt(c.getColumnIndexOrThrow(TaskEntry.COLUMN_COMPLETED)) > 0;
                assertThat(id, is(TASK.getId()));
                assertThat(description, is(TASK.getDescription()));
                assertThat(completed, is(TASK.getCompleted()));
            }
        }

        deleteAll();
    }

    @Test
    public void getTasks_checkResultValues() {
        // Save 3 stubbedTasks
        mLocalDataSource.saveTask(TASKS.get(0));
        mLocalDataSource.saveTask(TASKS.get(1));

        // Get the saved tasks by memberId
        String memberId = TASKS.get(0).getMemberId();
        mLocalDataSource.getTasksOfMember(memberId, new TaskDataSource.LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                assertNotNull(tasks);
                assertTrue(tasks.size() == 2);

                boolean task0Found = false;
                boolean task1Found = false;
                for (Task task : tasks) {
                    if (task.getId().equals(TASKS.get(0).getId())) {
                        task0Found = true;
                    }
                    if (task.getId().equals(TASKS.get(1).getId())) {
                        task1Found = true;
                    }
                }

                assertTrue(task0Found);
                assertTrue(task1Found);
            }

            @Override
            public void onDataNotAvailable() {
                fail();
            }
        });

        deleteAll();
    }

    @Test
    public void getTasks_firesOnTasksLoaded() {
        // Save 2 stubbedTasks
        mLocalDataSource.saveTask(TASKS.get(0));
        mLocalDataSource.saveTask(TASKS.get(1));

        TaskDataSource.LoadTasksCallback loadTasksCallback = Mockito.mock(TaskDataSource.LoadTasksCallback.class);
        String memberId = TASKS.get(0).getMemberId();
        mLocalDataSource.getTasksOfMember(memberId, loadTasksCallback);
        verify(loadTasksCallback).onTasksLoaded(anyListOf(Task.class));

        deleteAll();
    }

    @Test
    public void getTasksWithUnAvailableData_firesOnDataNotAvailable() {
        // without saving any tasks
        TaskDataSource.LoadTasksCallback loadTasksCallback = Mockito.mock(TaskDataSource.LoadTasksCallback.class);
        mLocalDataSource.getTasksOfMember("stubbedMemberId", loadTasksCallback);
        verify(loadTasksCallback, never()).onTasksLoaded(anyListOf(Task.class));
        verify(loadTasksCallback).onDataNotAvailable();
    }

    @Test
    public void deleteTasks_retrieveExistingTasks() {
        // Save 2 tasks
        mLocalDataSource.saveTask(TASKS.get(0));
        mLocalDataSource.saveTask(TASKS.get(1));
        String memberId = TASKS.get(1).getMemberId();

        // Delete tasks - index 0
        List<String> deletingIds = new ArrayList<>();
        deletingIds.add(TASKS.get(0).getId());
        mLocalDataSource.deleteTasks(deletingIds);

        // Retrieve tasks to verify that tasks are deleted
        mLocalDataSource.getTasksOfMember(memberId, new TaskDataSource.LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                assertTrue(tasks.size() == 1);
                assertThat(tasks.get(0).getId(), is(not(TASKS.get(0).getId())));
                assertThat(tasks.get(0).getId(), is(TASKS.get(1).getId()));
            }

            @Override
            public void onDataNotAvailable() {
                fail();
            }
        });
        deleteAll();
    }

    @Test
    public void updateTasks() {
        // Save 2 tasks
        final Task task0 = TASKS.get(0);
        mLocalDataSource.saveTask(task0);
        Task task1 = TASKS.get(1);
        mLocalDataSource.saveTask(task1);

        List<Task> updatingTasks = new ArrayList<>();
        final Task updatingTask0 = new Task(task0.getId(), task0.getMemberId(),
                "edit description0", false, task0.getOrder());
        updatingTasks.add(updatingTask0);
        final Task updatingTask1 = new Task(task1.getId(), task1.getMemberId(),
                "edit description", true, task0.getOrder());
        updatingTasks.add(updatingTask1);
        mLocalDataSource.editTasks(updatingTasks);

        mLocalDataSource.getTasksOfMember(TASKS.get(0).getMemberId(), new TaskDataSource.LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                assertNotNull(tasks);
                for (Task task : tasks) {
                    if (task.getId().equals(updatingTask0.getId())) {
                        assertEquals(updatingTask0.getDescription(), task.getDescription());
                        assertEquals(updatingTask0.getCompleted(), task.getCompleted());
                    }
                    if (task.getId().equals(updatingTask1.getId())) {
                        assertEquals(updatingTask1.getDescription(), task.getDescription());
                        assertEquals(updatingTask1.getCompleted(), task.getCompleted());
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
    public void getMembers() {
        // Save the stubbed members in taskHeadDetail
        saveStubbedTaskHeadDetail(TASKHEAD_DETAIL);

        mLocalDataSource.getMembers(TASKHEAD.getId(), new TaskDataSource.LoadMembersCallback() {
            @Override
            public void onMembersLoaded(List<Member> members) {
                assertNotNull(members);
                assertThat(members.size(), is(TASKHEAD_DETAIL.getMembers().size()));
                boolean foundMember0 = false;
                boolean foundMember1 = false;
                for(Member member:members) {
                    if(member.getId().equals(MEMBERS.get(0).getId())) {
                        foundMember0 = true;
                    }
                    if(member.getId().equals(MEMBERS.get(1).getId())) {
                        foundMember1 = true;
                    }
                }
                assertTrue(foundMember0);
                assertTrue(foundMember1);
            }

            @Override
            public void onDataNotAvailable() {
                fail();
            }
        });

        deleteAll();
        deleteAllTaskHeadDetails();
    }

    @Test
    public void getMembers_firesOnMembersLoaded() {

        saveStubbedTaskHeadDetail(TASKHEAD_DETAIL);

        TaskDataSource.LoadMembersCallback loadTasksCallback = Mockito.mock(TaskDataSource.LoadMembersCallback.class);
        mLocalDataSource.getMembers(TASKHEAD.getId(), loadTasksCallback);
        verify(loadTasksCallback).onMembersLoaded(anyListOf(Member.class));

        deleteAll();
    }

    @Test
    public void getTasksOfTaskHead_checkResultValues() {
        // Save 2 stubbedTasks
        mLocalDataSource.saveTask(TASKS.get(0));
        mLocalDataSource.saveTask(TASKS.get(1));

        TaskDataSource.LoadTasksCallback loadTasksCallback = Mockito.mock(TaskDataSource.LoadTasksCallback.class);
        String taskheadId = TASKHEAD.getId();
        mLocalDataSource.getTasksOfTaskHead(taskheadId, loadTasksCallback);
        verify(loadTasksCallback).onTasksLoaded(anyListOf(Task.class));
//        mLocalDataSource.getTasksOfTaskHead(taskheadId, new TaskDataSource.LoadTasksCallback() {
//            @Override
//            public void onTasksLoaded(List<Task> tasks) {
//                assertNotNull(tasks);
//                assertTrue(tasks.size() == 2);
//
//            }
//
//            @Override
//            public void onDataNotAvailable() {
//                fail();
//            }
//        });

        deleteAll();
    }
    private void deleteAllTaskHeadDetails() {
        mDbHelper.delete(PersistenceContract.TaskHeadEntry.TABLE_NAME, null, null);
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

    private void deleteAll() {
        mDbHelper.delete(TaskEntry.TABLE_NAME, null, null);
    }

    @After
    public void tearDown() {
        mDbHelper.close();
    }


}
