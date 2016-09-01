package com.kiwi.auready_ver2.data.source;

import org.junit.After;
import org.junit.Before;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for the implementation of the in-memory repository with cache.
 */
public class TaskRepositoryTest {

    private TaskRepository mTaskRepository;

    @Mock
    private TaskDataSource mTaskLocalDataSource;
    @Mock
    private TaskDataSource mTaskRemoteDataSource;
    @Mock
    private TaskDataSource.GetTasksCallback mGetTasksCallback;

    @Captor
    private ArgumentCaptor<TaskDataSource.GetTasksCallback> mTasksCallbackCaptor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mTaskRepository = TaskRepository.getInstance(
                mTaskRemoteDataSource, mTaskLocalDataSource);
    }

    @After
    public void destroyRepositoryInstance() {
        TaskRepository.destroyInstance();
    }

}