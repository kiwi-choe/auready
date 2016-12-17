package com.kiwi.auready_ver2.data.source;

import com.kiwi.auready_ver2.data.Task;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static com.kiwi.auready_ver2.StubbedData.TaskStub.TASKS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of the in-memory repository with cache.
 */
public class TaskRepositoryTest {

    private static final String TASKHEAD_ID = "stub_taskHeadId";
    private static final String MEMBER_ID = "stub_memberId";
    private static final String DESCRIPTION = "stub_description";

    /*
    * {@link Task}s stub that is added to the fake service API layer.
    * */
    // 3 tasks, one active and two completed of MEMBER the index 0
//    private static final List<Friend> MEMBERS = Lists.newArrayList(new Friend("email1", "name1"), new Friend("email2", "name2"),
//            new Friend("email3", "name3"));
//    private static TaskHead TASKHEAD = new TaskHead("title1", MEMBERS);
//
//    private static List<Task> TASKS = Lists.newArrayList(
//            new Task(TASKHEAD.getId(), MEMBERS.get(0).getId(), "description", 0),
//            new Task(TASKHEAD.getId(), MEMBERS.get(0).getId(), "description2", true, 0),
//            new Task(TASKHEAD.getId(), MEMBERS.get(0).getId(), "description3", true, 0));

    private TaskRepository mTaskRepository;

    @Mock
    private TaskDataSource mTaskLocalDataSource;
    @Mock
    private TaskDataSource mTaskRemoteDataSource;
    @Mock
    private TaskDataSource.LoadTasksCallback mLoadTasksCallback;
@Mock
private TaskDataSource.DeleteTasksCallback mDeleteTasksCallback;

    @Captor
    private ArgumentCaptor<TaskDataSource.LoadTasksCallback> mLoadTasksCallbackCaptor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mTaskRepository = TaskRepository.getInstance(
                mTaskRemoteDataSource, mTaskLocalDataSource);
    }

    @Test
    public void saveTask_savesTaskToServiceApi() {
        Task newTask = new Task(TASKHEAD_ID, MEMBER_ID, DESCRIPTION, 0);
        mTaskRepository.saveTask(newTask);

        // Then the service API are called and cache is updated.
        verify(mTaskRemoteDataSource).saveTask(eq(newTask));

        assertThat(mTaskRepository.mCachedTasksById.size(), is(1));
    }

    @Test
    public void saveTask_ToLocalDataSource() {
        Task newTask = new Task(TASKHEAD_ID, MEMBER_ID, DESCRIPTION, 0);
        mTaskRepository.saveTask(newTask);

        verify(mTaskLocalDataSource).saveTask(eq(newTask));
        assertThat(mTaskRepository.mCachedTasksById.size(), is(1));
    }

    @Test
    public void saveTask_checkOrderIsCorrect() {
        // Given 1 stub task
        int initial_order = 0;
        Task task1 = new Task(TASKHEAD_ID, MEMBER_ID, DESCRIPTION, initial_order);
        mTaskRepository.saveTask(task1);

        // Create new task
        // get the size of tasks
        int order = mTaskRepository.mCachedTasksByTaskMapKey.get(new TaskMapKey(TASKHEAD_ID, MEMBER_ID)).size();
        Task newTask = new Task(TASKHEAD_ID, MEMBER_ID, "new description", order);
        mTaskRepository.saveTask(newTask);

        assertThat(mTaskRepository.mCachedTasksById.size(), is(2));
        assertTrue(mTaskRepository.mCachedTasksById.get(newTask.getId()).getOrder() == newTask.getOrder());
    }

    @Test
    public void deleteTask_deleteTaskToLocalAndRemote() {
        // Save task
        Task newTask = new Task(TASKHEAD_ID, MEMBER_ID, DESCRIPTION, 0);
        mTaskRepository.saveTask(newTask);
        assertThat(mTaskRepository.mCachedTasksById.containsKey(newTask.getId()), is(true));

        // Delete the task
        mTaskRepository.deleteTask(newTask.getId());
        // Verify the data sources were called
        verify(mTaskRemoteDataSource).deleteTask(newTask.getId());
        verify(mTaskLocalDataSource).deleteTask(newTask.getId());
        // Verify cache is removed
        assertThat(mTaskRepository.mCachedTasksById.containsKey(newTask.getId()), is(false));
    }

    @Test
    public void deleteTasks_byTaskHeadId() {
        saveStubbedTasks(TASKS);

        String taskHeadId = TASKS.get(0).getTaskHeadId();
        mTaskRepository.deleteTasks(taskHeadId, mDeleteTasksCallback);
        verify(mTaskLocalDataSource).deleteTasks(taskHeadId, mDeleteTasksCallback);

        assertThat(mTaskRepository.mCachedTasksByTaskHeadId.containsKey(taskHeadId), is(false));
    }

    @Test
    public void deleteTasks_OfMember() {
        saveStubbedTasks(TASKS);

        String taskHeadId = TASKS.get(0).getTaskHeadId();
        String memberId = TASKS.get(0).getMemberId();
        mTaskRepository.deleteTasks(taskHeadId, memberId);
        verify(mTaskLocalDataSource).deleteTasks(taskHeadId, memberId);

        assertThat(mTaskRepository.mCachedTasksByTaskMapKey.containsKey(
                new TaskMapKey(taskHeadId, memberId)), is(false));
    }

    @Test
    public void getTasksOfMember_fromLocal() {
        mTaskRepository.getTasks(TASKHEAD_ID, MEMBER_ID, mLoadTasksCallback);

        setTasksAvailable(TASKHEAD_ID, MEMBER_ID, mTaskLocalDataSource, TASKS);

        verify(mLoadTasksCallback).onTasksLoaded(TASKS);
    }

    @Test
    public void getTasksOfMember_fromRemote() {
        mTaskRepository.getTasks(TASKHEAD_ID, MEMBER_ID, mLoadTasksCallback);

        // Local source has no data available,
        setTasksNotAvailable(TASKHEAD_ID, MEMBER_ID, mTaskLocalDataSource);
        // and call to Remote has data available
        setTasksAvailable(TASKHEAD_ID, MEMBER_ID, mTaskRemoteDataSource, TASKS);

        // Verify the tasks from Remote are returned, not Local
        verify(mTaskLocalDataSource, never()).
                getTasks(TASKHEAD_ID, MEMBER_ID, mLoadTasksCallback);
        verify(mLoadTasksCallback).onTasksLoaded(TASKS);
    }

    @Test
    public void getTasks_refreshesLocal() {
        mTaskRepository.getTasks(TASKHEAD_ID, MEMBER_ID, mLoadTasksCallback);

        // Make Local cannot return data
        setTasksNotAvailable(TASKHEAD_ID, MEMBER_ID, mTaskLocalDataSource);
        // Make Remote return data
        setTasksAvailable(TASKHEAD_ID, MEMBER_ID, mTaskRemoteDataSource, TASKS);

        // Verify that data fetched from Remote was saved in Local
        verify(mTaskLocalDataSource, times(TASKS.size())).saveTask(any(Task.class));
    }

    @Test
    public void getTasksOfTaskHead_fromLocal() {
        mTaskRepository.getTasks(TASKHEAD_ID, mLoadTasksCallback);

        // Set tasks available from local
        verify(mTaskLocalDataSource).getTasks(eq(TASKHEAD_ID), mLoadTasksCallbackCaptor.capture());
        mLoadTasksCallbackCaptor.getValue().onTasksLoaded(TASKS);

        verify(mLoadTasksCallback).onTasksLoaded(TASKS);
    }

    @Test
    public void getTasksOfTaskHead_fromRemote() {
        mTaskRepository.getTasks(TASKHEAD_ID, mLoadTasksCallback);

        // Local has no data available,
        verify(mTaskLocalDataSource).getTasks(eq(TASKHEAD_ID), mLoadTasksCallbackCaptor.capture());
        mLoadTasksCallbackCaptor.getValue().onDataNotAvailable();
        // and call to Remote has data available
        verify(mTaskRemoteDataSource).getTasks(eq(TASKHEAD_ID), mLoadTasksCallbackCaptor.capture());
        mLoadTasksCallbackCaptor.getValue().onTasksLoaded(TASKS);

        // Verify the tasks from Remote are returned, not local
        verify(mTaskLocalDataSource, never()).getTasks(TASKHEAD_ID, mLoadTasksCallback);
        verify(mLoadTasksCallback).onTasksLoaded(TASKS);
    }

    @Test
    public void getTaskWithBothDataSourceUnavailable_firesOnDataUnavailable() {
        mTaskRepository.getTasks(TASKHEAD_ID, MEMBER_ID, mLoadTasksCallback);

        setTasksNotAvailable(TASKHEAD_ID, MEMBER_ID, mTaskLocalDataSource);
        setTasksNotAvailable(TASKHEAD_ID, MEMBER_ID, mTaskRemoteDataSource);

        verify(mLoadTasksCallback).onDataNotAvailable();
    }

    /*
    * convenience methods
    * */
    private void setTasksAvailable(String taskHeadId, String memberId, TaskDataSource dataSource, List<Task> tasks) {
        verify(dataSource).getTasks(eq(taskHeadId), eq(memberId), mLoadTasksCallbackCaptor.capture());
        mLoadTasksCallbackCaptor.getValue().onTasksLoaded(tasks);
    }

    private void setTasksNotAvailable(String taskHeadId, String memberId, TaskDataSource dataSource) {
        verify(dataSource).getTasks(eq(taskHeadId), eq(memberId), mLoadTasksCallbackCaptor.capture());
        mLoadTasksCallbackCaptor.getValue().onDataNotAvailable();
    }

    private void saveStubbedTasks(List<Task> tasks) {
        mTaskRepository.saveTask(tasks.get(0));
        mTaskRepository.saveTask(tasks.get(1));
        mTaskRepository.saveTask(tasks.get(2));
    }

    @After
    public void destroyRepositoryInstance() {
        TaskRepository.destroyInstance();
    }
}