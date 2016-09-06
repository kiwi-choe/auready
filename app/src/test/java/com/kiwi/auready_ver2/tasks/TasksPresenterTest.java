package com.kiwi.auready_ver2.tasks;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.TestUseCaseScheduler;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.data.source.TaskDataSource;
import com.kiwi.auready_ver2.data.source.TaskRepository;
import com.kiwi.auready_ver2.tasks.domain.usecase.GetTasks;
import com.kiwi.auready_ver2.tasks.domain.usecase.SaveTask;
import com.kiwi.auready_ver2.tasks.domain.usecase.SaveTasks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the implementation of {@link TasksPresenter}.
 */
public class TasksPresenterTest {

    private static final String INVALID_TASKHEAD_ID = "";
    private static final String TASKHEAD_ID = "stubTaskHeadId";
    private static final String TASK_DESCRIPTION1 = "someday";
    private static final String TASK_DESCRIPTION2 = "we will know";
    private static final String TASK_DESCRIPTION3 = "OK?";

    /*
    * {@link Task}s stub that is added to the fake service API layer.
    * */
    // 3 tasks, one active and two completed
    private static List<Task> TASKS = Lists.newArrayList(new Task(TASKHEAD_ID, TASK_DESCRIPTION1),
            new Task(TASKHEAD_ID, TASK_DESCRIPTION2, true), new Task(TASKHEAD_ID, TASK_DESCRIPTION3, true));

    private TasksPresenter mTasksPresenter;

    @Mock
    private TasksContract.View mTasksView;
    @Mock
    private TaskRepository mTaskRepository;
    @Captor
    private ArgumentCaptor<TaskDataSource.GetTasksCallback> mLoadTasksCallbackCaptor;
    @Captor
    private ArgumentCaptor<TaskDataSource.SaveTaskCallback> mSaveTaskCallbackCaptor;

    @Before
    public void setup() {

        MockitoAnnotations.initMocks(this);

        // The presenter won't update the view unless it's active
        when(mTasksView.isActive()).thenReturn(true);
    }

    private TasksPresenter givenTasksPresenter(String taskHeadId) {
        UseCaseHandler useCaseHandler = new UseCaseHandler(new TestUseCaseScheduler());
        GetTasks getTasks = new GetTasks(mTaskRepository);
        SaveTasks saveTasks = new SaveTasks(mTaskRepository);
        SaveTask saveTask = new SaveTask(mTaskRepository);

        return new TasksPresenter(useCaseHandler, taskHeadId, mTasksView, getTasks, saveTasks, saveTask);
    }

    @Test
    public void loadTasksAndLoadIntoView() {
        mTasksPresenter = givenTasksPresenter(TASKHEAD_ID);
        mTasksPresenter.loadTasks();

        verify(mTaskRepository).getTasks(eq(TASKHEAD_ID), mLoadTasksCallbackCaptor.capture());
        mLoadTasksCallbackCaptor.getValue().onTasksLoaded(TASKS);

        ArgumentCaptor<List> showTasksArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(mTasksView).showTasks(showTasksArgumentCaptor.capture());
        assertTrue(showTasksArgumentCaptor.getValue().size() == 3);
    }

    @Test
    public void saveNewTask() {
        mTasksPresenter = givenTasksPresenter(TASKHEAD_ID); // Set mTaskHeadId in Presenter

        // Given Stub taskHeadId and Task Object
        Task newTask = new Task(TASKHEAD_ID);
        mTasksPresenter.saveTask(newTask);

        verify(mTaskRepository).saveTask(eq(newTask), mSaveTaskCallbackCaptor.capture());
        mSaveTaskCallbackCaptor.getValue().onTaskSaved();
    }

    @Test
    public void saveTaskHead_emptyTaskHeadShowsErrorUi() {
        mTasksPresenter = givenTasksPresenter(TASKHEAD_ID);
        // Check that there is no tasks and title
        mTasksPresenter.validateEmptyTaskHead("", 0);
        verify(mTasksView).showEmptyTaskHeadError();
    }

    @Test
    public void filterActiveTasks_showIntoActiveTasksView() {
        mTasksPresenter = givenTasksPresenter(TASKHEAD_ID);
        mTasksPresenter.loadTasks();

        verify(mTaskRepository).getTasks(eq(TASKHEAD_ID), mLoadTasksCallbackCaptor.capture());
        mLoadTasksCallbackCaptor.getValue().onTasksLoaded(TASKS);

        ArgumentCaptor<List> showActiveTasksArgumentCaptor =  ArgumentCaptor.forClass(List.class);
        verify(mTasksView).showTasks(showActiveTasksArgumentCaptor.capture());
        assertTrue(showActiveTasksArgumentCaptor.getValue().size() == 1);
    }

    @Test
    public void tasksAreNotShownWhenTasksIsEmpty() {
        mTasksPresenter = givenTasksPresenter(TASKHEAD_ID);
        mTasksPresenter.loadTasks();

        verify(mTaskRepository).getTasks(eq(TASKHEAD_ID), mLoadTasksCallbackCaptor.capture());
        mLoadTasksCallbackCaptor.getValue().onDataNotAvailable();

        verify(mTasksView).showEmptyTaskHeadError();
    }
    @Test
    public void tasksAreNotShownWhenInvalidTaskHeadId() {
        mTasksPresenter = givenTasksPresenter(INVALID_TASKHEAD_ID);
        mTasksPresenter.loadTasks();

        verify(mTasksView).showEmptyTaskHeadError();
    }



}