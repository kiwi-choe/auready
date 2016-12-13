package com.kiwi.auready_ver2.data.source;

import com.kiwi.auready_ver2.data.Task;

import org.bouncycastle.jcajce.provider.symmetric.DES;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of the in-memory repository with cache.
 */
public class TaskRepositoryTest {

    private static final String TASKHEAD_ID = "stub_taskHeadId";
    private static final String MEMBER_ID = "stub_memberId";
    private static final String DESCRIPTION = "stub_description";

    private TaskRepository mTaskRepository;

    @Mock
    private TaskDataSource mTaskLocalDataSource;
    @Mock
    private TaskDataSource mTaskRemoteDataSource;
    @Mock
    private TaskDataSource.GetTasksCallback mGetTasksCallback;


    @Captor
    private ArgumentCaptor<TaskDataSource.GetTasksCallback> mGetTasksCallbackCaptor;

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

        assertThat(mTaskRepository.mCachedTasks.size(), is(1));
    }

    @Test
    public void saveTask_ToLocalDataSource() {
        Task newTask = new Task(TASKHEAD_ID, MEMBER_ID, DESCRIPTION, 0);
        mTaskRepository.saveTask(newTask);

        verify(mTaskLocalDataSource).saveTask(eq(newTask));
        assertThat(mTaskRepository.mCachedTasks.size(), is(1));
    }

    @Test
    public void saveTask_checkOrderIsCorrect() {
        // Given 1 stub task
        int initial_order = 0;
        Task task1 = new Task(TASKHEAD_ID, MEMBER_ID, DESCRIPTION, initial_order);
        mTaskRepository.saveTask(task1);

        // Create new task
        // get the size of tasks
        int order = mTaskRepository.mCachedTasks.get(new TaskMapKey(TASKHEAD_ID, MEMBER_ID)).size();
        Task newTask = new Task(TASKHEAD_ID, MEMBER_ID, "new description", order);
        mTaskRepository.saveTask(newTask);

        assertThat(mTaskRepository.mCachedTasks.get(new TaskMapKey(TASKHEAD_ID, MEMBER_ID)).size(), is(2));
        assertTrue(mTaskRepository.mCachedTasks.get(
                new TaskMapKey(TASKHEAD_ID, MEMBER_ID)).get(newTask.getId()).getOrder() == newTask.getOrder());
    }

    @After
    public void destroyRepositoryInstance() {
        TaskRepository.destroyInstance();
    }
}