package com.kiwi.auready_ver2.data.local;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.common.collect.Lists;
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
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    @Mock
    private TaskDataSource.SaveTaskCallback mSaveCallback;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mDbHelper = SQLiteDBHelper.getInstance(RuntimeEnvironment.application);
        // Save member first
        saveStubbedTaskHeadDetail(TASKHEAD_DETAIL);
    }

    @Test
    public void saveTask() {
        mLocalDataSource.saveTask(TASK, new ArrayList<Task>(), mSaveCallback);

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
    }

    @Test
    public void replaceTask_canUpdateTheExistingTasks() {
        mLocalDataSource.saveTask(TASK, new ArrayList<Task>(), mSaveCallback);
        String id = TASK.getId();
        Task updatedTask = new Task(id, TASK.getMemberId(), "editDes", false, 0);
        mLocalDataSource.saveTask(updatedTask, new ArrayList<Task>(), mSaveCallback);

        // Retrieve the saved task using query directly
        String selection = TaskEntry.COLUMN_ID + " LIKE?";
        String[] selectionArgs = {TASK.getId()};
        Cursor c = mDbHelper.query(TaskEntry.TABLE_NAME, null, selection, selectionArgs, null, null, null);
        if (c != null && c.getCount() > 0) {
            assertEquals(1, c.getCount());

            while (c.moveToNext()) {
                String taskId = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_ID));
                String description = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_DESCRIPTION));
                assertThat(taskId, is(id));
                assertThat(description, is("editDes"));
            }
        }
    }

    @Test
    public void getTasks_checkResultValues() {
        // Save 3 stubbedTasks
        mLocalDataSource.saveTask(TASKS.get(0), new ArrayList<Task>(), mSaveCallback);
        mLocalDataSource.saveTask(TASKS.get(1), new ArrayList<Task>(), mSaveCallback);

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
    }

    @Test
    public void getTasks_firesOnTasksLoaded() {
        // Save 2 stubbedTasks
        mLocalDataSource.saveTask(TASKS.get(0), new ArrayList<Task>(), mSaveCallback);
        mLocalDataSource.saveTask(TASKS.get(1), new ArrayList<Task>(), mSaveCallback);

        TaskDataSource.LoadTasksCallback loadTasksCallback = Mockito.mock(TaskDataSource.LoadTasksCallback.class);
        String memberId = TASKS.get(0).getMemberId();
        mLocalDataSource.getTasksOfMember(memberId, loadTasksCallback);
        verify(loadTasksCallback).onTasksLoaded(anyListOf(Task.class));
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
    public void deleteTask_retrieveExistingTasks() {
        // Save 2 tasks
        mLocalDataSource.saveTask(TASKS.get(0), new ArrayList<Task>(), mSaveCallback);
        mLocalDataSource.saveTask(TASKS.get(1), new ArrayList<Task>(), mSaveCallback);
        String memberId = TASKS.get(0).getMemberId();

        // Delete tasks - index 0
        TaskDataSource.DeleteTaskCallback deleteCallback = Mockito.mock(TaskDataSource.DeleteTaskCallback.class);
        mLocalDataSource.deleteTask(memberId, TASKS.get(0).getId(), new ArrayList<Task>(), deleteCallback);

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
    }

    @Test
    public void updateTasks() {
        String memberId0 = MEMBERS.get(0).getId();
        String memberId1 = MEMBERS.get(1).getId();
        final List<Task> TASKS0 = Lists.newArrayList(
                new Task("stubbedTask0", memberId0, "description", 0),
                new Task("stubbedTask1", memberId0, "description2", true, 0));
        final List<Task> TASKS1 = Lists.newArrayList(
                new Task("stubbedTask2", memberId1, "description3", true, 0));

        // Save tasks
        Map<String, List<Task>> cachedTasks = new LinkedHashMap<>();
        cachedTasks.put(memberId0, TASKS0);
        cachedTasks.put(memberId1, TASKS1);
        for (Task task : TASKS0) {
            mLocalDataSource.saveTask(task, new ArrayList<Task>(), mSaveCallback);
        }
        for (Task task : TASKS0) {
            mLocalDataSource.saveTask(task, new ArrayList<Task>(), mSaveCallback);
        }

        // Update tasks
        TASKS0.get(0).setDescription("editDescription!!!");
        cachedTasks.put(memberId0, TASKS0);
        TASKS1.get(0).setDescription("edit description3");
        cachedTasks.put(memberId1, TASKS1);

        // Make the collection for all the tasks of members
        List<Task> updatingTasks = new ArrayList<>();
        for (String key : cachedTasks.keySet()) {
            List<Task> tasks = cachedTasks.get(key);
            updatingTasks.addAll(tasks);
        }

        // Query
        String whereClause = TaskEntry.COLUMN_ID + " LIKE?";
        for (Task task : updatingTasks) {
            ContentValues values = new ContentValues();
            values.put(TaskEntry.COLUMN_DESCRIPTION, task.getDescription());
            values.put(TaskEntry.COLUMN_COMPLETED, task.getCompletedInteger());
            values.put(TaskEntry.COLUMN_ORDER, task.getOrder());

            String[] whereArgs = {task.getId()};
            mDbHelper.update(TaskEntry.TABLE_NAME, values, whereClause, whereArgs);
        }

        mLocalDataSource.getTasksOfMember(memberId0, new TaskDataSource.LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                assertNotNull(tasks);
                for (Task task : tasks) {
                    if (task.getId().equals(TASKS0.get(0).getId())) {
                        assertEquals(TASKS0.get(0).getDescription(), task.getDescription());
                    }
                    if (task.getId().equals(TASKS1.get(0).getId())) {
                        assertEquals(TASKS1.get(0).getDescription(), task.getDescription());
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
    public void editTasksOfMember() {
        // save stubbedTasks
        for (Task task : TASKS) {
            mLocalDataSource.saveTask(task, new ArrayList<Task>(), mSaveCallback);
        }
        // Update tasks
        TASKS.get(0).setDescription("EDIT DES 0");
        TASKS.get(1).setDescription("EDIT DES 1");

        // Make the collection for all the tasks of members
        List<Task> updatingTasks = new ArrayList<>();
        updatingTasks.addAll(TASKS);

        TaskDataSource.EditTasksOfMemberCallback editCallback = Mockito.mock(TaskDataSource.EditTasksOfMemberCallback.class);
        mLocalDataSource.editTasksOfMember(TASKS.get(0).getMemberId(), updatingTasks, editCallback);

        mLocalDataSource.getTasksOfMember(TASKS.get(0).getMemberId(), new TaskDataSource.LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                assertNotNull(tasks);
                for (Task task : tasks) {
                    if (task.getId().equals(TASKS.get(0).getId())) {
                        assertEquals(TASKS.get(0).getDescription(), task.getDescription());
                    }
                    if (task.getId().equals(TASKS.get(1).getId())) {
                        assertEquals(TASKS.get(1).getDescription(), task.getDescription());
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

        mLocalDataSource.getMembers(TASKHEAD.getId(), new TaskDataSource.LoadMembersCallback() {
            @Override
            public void onMembersLoaded(List<Member> members) {
                assertNotNull(members);
                assertThat(members.size(), is(TASKHEAD_DETAIL.getMembers().size()));
                boolean foundMember0 = false;
                boolean foundMember1 = false;
                for (Member member : members) {
                    if (member.getId().equals(MEMBERS.get(0).getId())) {
                        foundMember0 = true;
                    }
                    if (member.getId().equals(MEMBERS.get(1).getId())) {
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
    }

    @Test
    public void getMembers_firesOnMembersLoaded() {

        TaskDataSource.LoadMembersCallback loadTasksCallback = Mockito.mock(TaskDataSource.LoadMembersCallback.class);
        mLocalDataSource.getMembers(TASKHEAD.getId(), loadTasksCallback);
        verify(loadTasksCallback).onMembersLoaded(anyListOf(Member.class));
    }

    @Test
    public void getTasksOfTaskHead_checkResultValues() {
        // Save 2 stubbedTasks
        mLocalDataSource.saveTask(TASKS.get(0), new ArrayList<Task>(), mSaveCallback);
        verify(mSaveCallback).onSaveSuccess(new ArrayList<Task>());

        // retrieve in Task table
        String query = String.format(
                "SELECT %s.%s, %s, %s, %s, %s.%s FROM %s " +
                        "INNER JOIN %s ON %s.%s = %s.%s " +
                        "INNER JOIN %s ON %s.%s = %s.%s " +
                        "WHERE %s.%s = \'%s\'",
                TaskEntry.TABLE_NAME, TaskEntry.COLUMN_ID,
                TaskEntry.COLUMN_MEMBER_ID_FK,
                TaskEntry.COLUMN_DESCRIPTION,
                TaskEntry.COLUMN_COMPLETED,
                TaskEntry.TABLE_NAME, TaskEntry.COLUMN_ORDER,
                TaskEntry.TABLE_NAME,
                PersistenceContract.MemberEntry.TABLE_NAME, PersistenceContract.MemberEntry.TABLE_NAME, PersistenceContract.MemberEntry.COLUMN_ID, TaskEntry.TABLE_NAME, TaskEntry.COLUMN_MEMBER_ID_FK,
                PersistenceContract.TaskHeadEntry.TABLE_NAME, PersistenceContract.TaskHeadEntry.TABLE_NAME, PersistenceContract.TaskHeadEntry.COLUMN_ID, PersistenceContract.MemberEntry.TABLE_NAME, PersistenceContract.MemberEntry.COLUMN_HEAD_ID_FK,
                PersistenceContract.TaskHeadEntry.TABLE_NAME, PersistenceContract.TaskHeadEntry.COLUMN_ID, TASKHEAD.getId()
        );
        Cursor c = mDbHelper.rawQuery(query, null);
        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                assertEquals(1, c.getCount());
                String id = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_ID));
                String memberId = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_MEMBER_ID_FK));
                String description = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_DESCRIPTION));
                boolean completed = (c.getInt(c.getColumnIndexOrThrow(TaskEntry.COLUMN_COMPLETED)) > 0);
                int order = c.getInt(c.getColumnIndexOrThrow(TaskEntry.COLUMN_ORDER));

                assertEquals(id, TASKS.get(0).getId());
                assertEquals(memberId, TASKS.get(0).getMemberId());
                assertEquals(description, TASKS.get(0).getDescription());
                assertEquals(completed, TASKS.get(0).getCompleted());
                assertEquals(order, TASKS.get(0).getOrder());
            }
        } else {
            fail();
        }

        TaskDataSource.LoadTasksCallback loadTasksCallback = Mockito.mock(TaskDataSource.LoadTasksCallback.class);
        String taskheadId = TASKHEAD.getId();
        mLocalDataSource.getTasksOfTaskHead(taskheadId, loadTasksCallback);
        verify(loadTasksCallback).onTasksLoaded(anyListOf(Task.class));
    }

    // Delete tasks of MEMBERS index 0
    @Test
    public void deleteTasksOfMember() {
        mLocalDataSource.saveTask(TASKS.get(0), new ArrayList<Task>(), mSaveCallback);
        mLocalDataSource.saveTask(TASKS.get(1), new ArrayList<Task>(), mSaveCallback);

        mLocalDataSource.deleteTasksOfMember(TASKS.get(0).getMemberId());

        // Check
        mLocalDataSource.getTasksOfMember(TASKS.get(0).getMemberId(), new TaskDataSource.LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                fail();
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
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
        deleteAll();
        deleteAllTaskHeadDetails();
        mDbHelper.close();
    }


}
