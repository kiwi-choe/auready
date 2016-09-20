package com.kiwi.auready_ver2.tasks;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.TestUseCaseScheduler;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.data.source.TaskDataSource;
import com.kiwi.auready_ver2.data.source.TaskRepository;
import com.kiwi.auready_ver2.tasks.domain.usecase.ActivateTask;
import com.kiwi.auready_ver2.tasks.domain.usecase.CompleteTask;
import com.kiwi.auready_ver2.tasks.domain.usecase.DeleteTask;
import com.kiwi.auready_ver2.tasks.domain.usecase.GetTasks;
import com.kiwi.auready_ver2.tasks.domain.usecase.SaveTask;
import com.kiwi.auready_ver2.tasks.domain.usecase.SaveTasks;
import com.kiwi.auready_ver2.tasks.domain.usecase.SortTasks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.LinkedHashMap;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
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
        CompleteTask completeTask = new CompleteTask(mTaskRepository);
        ActivateTask activateTask = new ActivateTask(mTaskRepository);
        SortTasks sortTasks = new SortTasks(mTaskRepository);
        DeleteTask deleteTask = new DeleteTask(mTaskRepository);

        return new TasksPresenter(useCaseHandler, taskHeadId, mTasksView,
                getTasks, saveTasks, saveTask, completeTask, activateTask, sortTasks, deleteTask);
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
    public void saveTaskHead_emptyTaskHeadShowsErrorUi() {
        mTasksPresenter = givenTasksPresenter(TASKHEAD_ID);
        // Check that there is no tasks and title
        boolean isEmptyTaskHead = mTasksPresenter.validateEmptyTaskHead("", 0);
        assertTrue(isEmptyTaskHead);
    }

    @Test
    public void completeTask() {
        mTasksPresenter = givenTasksPresenter(TASKHEAD_ID);

        Task activeTask = new Task(TASKHEAD_ID, TASK_DESCRIPTION1);

        mTasksPresenter.completeTask(activeTask);
        // Then a request is sent to the task repository and the UI is updated.
        verify(mTaskRepository).completeTask(activeTask);
    }

    @Test
    public void activateTask() {
        mTasksPresenter = givenTasksPresenter(TASKHEAD_ID);

        Task completeTask = new Task(TASKHEAD_ID, TASK_DESCRIPTION1);
        mTasksPresenter.activateTask(completeTask);

        verify(mTaskRepository).activateTask(completeTask);
    }


//    @Test
//    public void filterActiveTasks_showIntoActiveTasksView() {
//        mTasksPresenter = givenTasksPresenter(TASKHEAD_ID);
//        mTasksPresenter.loadTasks();
//
//        verify(mTaskRepository).getTasks(eq(TASKHEAD_ID), mLoadTasksCallbackCaptor.capture());
//        mLoadTasksCallbackCaptor.getValue().onTasksLoaded(TASKS);
//
//        ArgumentCaptor<List> showActiveTasksArgumentCaptor =  ArgumentCaptor.forClass(List.class);
//        verify(mTasksView).showTasks(showActiveTasksArgumentCaptor.capture());
//        assertTrue(showActiveTasksArgumentCaptor.getValue().size() == 1);
//    }

    @Test
    public void tasksAreNotShownWhenTasksIsEmpty() {
        mTasksPresenter = givenTasksPresenter(TASKHEAD_ID);
        mTasksPresenter.loadTasks();

        verify(mTaskRepository).getTasks(eq(TASKHEAD_ID), mLoadTasksCallbackCaptor.capture());
        mLoadTasksCallbackCaptor.getValue().onDataNotAvailable();

        verify(mTasksView).showInvalidTaskHeadError();
    }
    @Test
    public void tasksAreNotShownWhenInvalidTaskHeadId() {
        mTasksPresenter = givenTasksPresenter(INVALID_TASKHEAD_ID);
        mTasksPresenter.loadTasks();

        verify(mTasksView).showInvalidTaskHeadError();
    }

//    Modifying

    @Test
    public void addTask() {
        mTasksPresenter = givenTasksPresenter(TASKHEAD_ID);

        Task newTask = new Task(TASKHEAD_ID);
        mTasksPresenter.addTask(newTask);

        assertThat(mTasksPresenter.mTaskList.size(), is(1));

        // 1. Save a task
        verify(mTaskRepository).saveTask(any(Task.class), mSaveTaskCallbackCaptor.capture());
        mSaveTaskCallbackCaptor.getValue().onTaskSaved();
        // 2. Update tasks(order)
        verify(mTaskRepository).sortTasks(any(LinkedHashMap.class));
    }

    @Test
    public void editTask_description() {
        mTasksPresenter = givenTasksPresenter(TASKHEAD_ID);

        // Save a task
        Task newTask = new Task(TASKHEAD_ID);
        mTasksPresenter.addTask(newTask);

        String description = "DESCRIPTION";
        Task editedTask = new Task(TASKHEAD_ID, newTask.getId(), description);
        mTasksPresenter.editTask(editedTask);

        // Update description of the existing task
        assertThat(mTasksPresenter.mTaskList.get(editedTask.getId()).getDescription(), is(description));
        // Save the existing task
        verify(mTaskRepository, times(2)).saveTask(any(Task.class), mSaveTaskCallbackCaptor.capture());
        mSaveTaskCallbackCaptor.getValue().onTaskSaved();
    }

    @Test
    public void deleteTask() {
        mTasksPresenter = givenTasksPresenter(TASKHEAD_ID);

        // Save 2 tasks
        Task task1 = new Task(TASKHEAD_ID);
        mTasksPresenter.addTask(task1);
        Task task2 = new Task(TASKHEAD_ID);
        mTasksPresenter.addTask(task2);

        assertThat(mTasksPresenter.mTaskList.size(), is(2));
        // 1. Delete one task
        mTasksPresenter.deleteTask(task1);
        assertThat(mTasksPresenter.mTaskList.size(), is(1));
        verify(mTaskRepository).deleteTask(task1);
        // 2. Update tasks(order)
        verify(mTaskRepository).sortTasks(any(LinkedHashMap.class));
    }
}