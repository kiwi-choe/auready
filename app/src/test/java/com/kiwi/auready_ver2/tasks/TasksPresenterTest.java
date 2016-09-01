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

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the implementation of {@link TasksPresenter}.
 */
public class TasksPresenterTest {

    private static String TASKHEAD_ID = "stubTaskHeadId";
    private static final String INVALID_TASKHEAD_ID = "";

    private static List<Task> TASKS;


    private TasksPresenter mTasksPresenter;

    @Mock
    private TasksContract.View mTasksView;
    @Mock
    private TaskRepository mTaskRepository;
    @Captor
    private ArgumentCaptor<TaskDataSource.GetTasksCallback> mLoadTasksCallbackCaptor;

    @Before
    public void setup() {

        MockitoAnnotations.initMocks(this);

        // 3 tasks, one active and two completed
        TASKS = Lists.newArrayList(new Task("taskheadId1", "description1"),
                new Task("taskheadId2", "description2", true), new Task("taskheadId3", "description3", true));

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
    public void filterActiveTasks_showIntoActiveTasksView() {
        mTasksPresenter = givenTasksPresenter(TASKHEAD_ID);
        mTasksPresenter.loadTasks();

        verify(mTaskRepository).getTasks(eq(TASKHEAD_ID), mLoadTasksCallbackCaptor.capture());
        mLoadTasksCallbackCaptor.getValue().onTasksLoaded(TASKS);

        ArgumentCaptor<List> showActiveTasksArgumentCaptor =  ArgumentCaptor.forClass(List.class);
        verify(mTasksView).showActiveTasks(showActiveTasksArgumentCaptor.capture());
        assertTrue(showActiveTasksArgumentCaptor.getValue().size() == 1);
    }
    @Test
    public void filterCompletedTasks_showIntoCompletedTasksView() {
        mTasksPresenter = givenTasksPresenter(TASKHEAD_ID);
        mTasksPresenter.loadTasks();

        verify(mTaskRepository).getTasks(eq(TASKHEAD_ID), mLoadTasksCallbackCaptor.capture());
        mLoadTasksCallbackCaptor.getValue().onTasksLoaded(TASKS);

        ArgumentCaptor<List> showCompletedTasksArgumentCaptor =  ArgumentCaptor.forClass(List.class);
        verify(mTasksView).showCompletedTasks(showCompletedTasksArgumentCaptor.capture());
        assertTrue(showCompletedTasksArgumentCaptor.getValue().size() == 2);
    }

    @Test
    public void tasksAreNotShownWhenTasksIsEmpty() {
        mTasksPresenter = givenTasksPresenter(TASKHEAD_ID);
        mTasksPresenter.loadTasks();

        verify(mTaskRepository).getTasks(eq(TASKHEAD_ID), mLoadTasksCallbackCaptor.capture());
        mLoadTasksCallbackCaptor.getValue().onDataNotAvailable();

        verify(mTasksView).showEmptyTasksError();
    }

    @Test
    public void tasksAreNotShownWhenInvalidTaskHeadId() {
        mTasksPresenter = givenTasksPresenter(INVALID_TASKHEAD_ID);
        mTasksPresenter.loadTasks();

        verify(mTasksView).showEmptyTasksError();
    }

    @Test
    public void onPause_sendExtrasToTaskHeadsView_withoutTitleAndTasks() {

        // When TaskHeadId is null,
        mTasksPresenter = givenTasksPresenter(null);

        String title = "";
        List<Task> tasks = new ArrayList<>(0);
        mTasksPresenter.saveTasks(title, tasks);

        boolean isEmpty = mTasksPresenter.isEmptyTaskHead(title, tasks);
        assertTrue(isEmpty);

        verify(mTasksView).showEmptyTasksError();
        fail();
    }

    @Test
    public void saveTask() {
        mTasksPresenter = givenTasksPresenter(null);

        // Given Stub taskHeadId and Task Obejct
        String taskHeadId = "11";
        Task newTask = new Task(taskHeadId);
        mTasksPresenter.saveTask(newTask);
        verify(mTaskRepository).saveTask(newTask);
        fail();
    }

    @Test
    public void saveTask_updateTasksView() {
        mTasksPresenter = givenTasksPresenter(null);

        // Given Stub taskHeadId and Task Obejct
        String taskHeadId = "11";
        Task newTask = new Task(taskHeadId);
        mTasksPresenter.saveTask(newTask);

        verify(mTaskRepository).saveTask(newTask);
        fail();
    }
}