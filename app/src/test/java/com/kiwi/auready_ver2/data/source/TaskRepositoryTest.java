package com.kiwi.auready_ver2.data.source;

import com.kiwi.auready_ver2.data.Task;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.LinkedList;

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
    private ArgumentCaptor<TaskDataSource.GetTasksCallback> mGetTasksCallbackCaptor;
    @Captor
    private ArgumentCaptor<TaskDataSource.SaveTaskCallback> mSaveTaskCallbackCaptor;

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
    public void saveTask_localDataSource() {
        Task newTask = new Task(TASKHEAD_ID);
        mTaskRepository.saveTask(newTask, mSaveTaskCallback);

        verify(mTaskRemoteDataSource).saveTask(eq(newTask), mSaveTaskCallbackCaptor.capture());
        mSaveTaskCallbackCaptor.getValue().onTaskSaved();

        verify(mTaskLocalDataSource).saveTask(eq(newTask), any(TaskDataSource.SaveTaskCallback.class));
    }

    @Test
    public void completeTask() {
        // Given a stub active task in the repository
        Task newTask = new Task(TASKHEAD_ID, "Im being with you", 0);
        mTaskRepository.saveTask(newTask, mSaveTaskCallback);

        mTaskRepository.completeTask(newTask);

//        verify(mTaskRemoteDataSource).completeTask(newTask);
        assertThat(mTaskRepository.mCachedTasks.size(), is(1));
        assertThat(mTaskRepository.mCachedTasks.get(TASKHEAD_ID).get(newTask.getId()).isActive(), is(false));
    }

    @Test
    public void activateTask() {
        // Given a stub complete task in the repository
        Task newTask = new Task(TASKHEAD_ID, "go sleep", true, 0);
        mTaskRepository.saveTask(newTask, mSaveTaskCallback);

        mTaskRepository.activateTask(newTask);

//        verify(mTaskRemoteDataSource).activateTask(newTask);
        assertThat(mTaskRepository.mCachedTasks.size(), is(1));
        assertThat(mTaskRepository.mCachedTasks.get(TASKHEAD_ID).get(newTask.getId()).isCompleted(), is(false));
    }

    @Test
    public void sortTasks() {
        // Given stubs one active, two complete
        Task task1 = new Task(TASKHEAD_ID);
        mTaskRepository.saveTask(task1, mSaveTaskCallback);
        Task task2 = new Task(TASKHEAD_ID, "completed one", true, 1);
        mTaskRepository.saveTask(task2, mSaveTaskCallback);
        Task task3 = new Task(TASKHEAD_ID, "completed two", true, 2);
        mTaskRepository.saveTask(task3, mSaveTaskCallback);

        // before order
        assertThat(mTaskRepository.mCachedTasks.get(TASKHEAD_ID).get(task2.getId()).getOrder(), is(1));

        LinkedList<Task> tasksAddedOne = new LinkedList<>();
        tasksAddedOne.add(task1);
        // add new task
        Task newTask = new Task(TASKHEAD_ID);
        tasksAddedOne.add(newTask);
        tasksAddedOne.add(task2);
        tasksAddedOne.add(task3);

        mTaskRepository.sortTasks(tasksAddedOne);
        verify(mTaskLocalDataSource).sortTasks(tasksAddedOne);
        // after order
        assertThat(mTaskRepository.mCachedTasks.get(TASKHEAD_ID).get(task2.getId()).getOrder(), is(2));
    }

    @Test
    public void deleteTask() {
        // Save 2 tasks
        Task task1 = new Task(TASKHEAD_ID, "task1", 0);
        mTaskRepository.saveTask(task1, mSaveTaskCallback);
        Task task2 = new Task(TASKHEAD_ID, "task2", 1);
        mTaskRepository.saveTask(task2, mSaveTaskCallback);
        assertThat(mTaskRepository.mCachedTasks.get(task1.getTaskHeadId()).containsKey(task1.getId()), is(true));

        // When deleted
        mTaskRepository.deleteTask(task1);
        // Verify the data sources were called
        verify(mTaskLocalDataSource).deleteTask(task1);
        // Verify it's removed from repository
        assertThat(mTaskRepository.mCachedTasks.get(task1.getTaskHeadId()).containsKey(task1.getId()), is(false));
    }

    @Test
    public void editDescription() {
        // Save 2 tasks
        Task task1 = new Task(TASKHEAD_ID, "task1", 0);
        mTaskRepository.saveTask(task1, mSaveTaskCallback);

        Task editTask = new Task(TASKHEAD_ID, task1.getId(), "editTask!!", task1.getOrder());
        mTaskRepository.editDescription(editTask);

        verify(mTaskLocalDataSource).editDescription(editTask);
        assertThat(mTaskRepository.mCachedTasks.get(task1.getTaskHeadId()).get(task1.getId()).getDescription(),
                is(editTask.getDescription()));
    }
    @After
    public void destroyRepositoryInstance() {
        TaskRepository.destroyInstance();
    }
}