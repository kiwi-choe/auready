package com.kiwi.auready_ver2.data.source;

import com.kiwi.auready_ver2.data.Task;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of the in-memory repository with cache.
 */
public class TaskRepositoryTest {

    private static final String TASKHEAD_ID = "stub_taskHeadId";

    private TaskRepository mTaskRepository;

    @Mock
    private TaskDataSource mTaskLocalDataSource;
    @Mock
    private TaskDataSource mTaskRemoteDataSource;
    @Mock
    private TaskDataSource.GetTasksCallback mGetTasksCallback;
    @Mock
    private TaskDataSource.SaveTaskCallback mSaveTaskCallback;

    @Captor
    private ArgumentCaptor<TaskDataSource.GetTasksCallback> mTasksCallbackCaptor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mTaskRepository = TaskRepository.getInstance(
                mTaskRemoteDataSource, mTaskLocalDataSource);
    }

    @Test
    public void saveTask_savesTaskToServiceApi() {
        Task newTask = new Task(TASKHEAD_ID);
        mTaskRepository.saveTask(newTask, mSaveTaskCallback);

        // Then the service API are called and cache is updated.
        verify(mTaskRemoteDataSource).saveTask(eq(newTask), any(TaskDataSource.SaveTaskCallback.class));
        assertThat(mTaskRepository.mCachedTasks.size(), is(1));
    }

    @Test
    public void completeTask() {
        // Given a stub active task in the repository
        Task newTask = new Task(TASKHEAD_ID, "Im being with you");
        mTaskRepository.saveTask(newTask, mSaveTaskCallback);

        mTaskRepository.completeTask(newTask);

        verify(mTaskRemoteDataSource).completeTask(newTask);
        assertThat(mTaskRepository.mCachedTasks.size(), is(1));
        assertThat(mTaskRepository.mCachedTasks.get(TASKHEAD_ID).get(newTask.getId()).isActive(), is(false));
    }

    @Test
    public void activateTask() {
        // Given a stub complete task in the repository
        Task newTask = new Task(TASKHEAD_ID, "go sleep", true);
        mTaskRepository.saveTask(newTask, mSaveTaskCallback);

        mTaskRepository.activateTask(newTask);

        verify(mTaskRemoteDataSource).activateTask(newTask);
        assertThat(mTaskRepository.mCachedTasks.size(), is(1));
        assertThat(mTaskRepository.mCachedTasks.get(TASKHEAD_ID).get(newTask.getId()).isCompleted(), is(false));
    }

    @After
    public void destroyRepositoryInstance() {
        TaskRepository.destroyInstance();
    }
}