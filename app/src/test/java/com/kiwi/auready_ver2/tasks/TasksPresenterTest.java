package com.kiwi.auready_ver2.tasks;

import com.kiwi.auready_ver2.TestUseCaseScheduler;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.data.source.TaskDataSource;
import com.kiwi.auready_ver2.data.source.TaskHeadDataSource;
import com.kiwi.auready_ver2.data.source.TaskHeadRepository;
import com.kiwi.auready_ver2.data.source.TaskRepository;
import com.kiwi.auready_ver2.taskheaddetail.domain.usecase.GetTaskHead;
import com.kiwi.auready_ver2.tasks.domain.usecase.DeleteTask;
import com.kiwi.auready_ver2.tasks.domain.usecase.GetTasksOfMember;
import com.kiwi.auready_ver2.tasks.domain.usecase.GetTasksOfTaskHead;
import com.kiwi.auready_ver2.tasks.domain.usecase.SaveTask;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static com.kiwi.auready_ver2.StubbedData.TaskStub.MEMBERS;
import static com.kiwi.auready_ver2.StubbedData.TaskStub.TASKHEAD;
import static com.kiwi.auready_ver2.StubbedData.TaskStub.TASKS;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of {@link TasksPresenter}.
 */
public class TasksPresenterTest {

    private TasksPresenter mTasksPresenter;

    @Mock
    private TasksContract.View mTasksView;
    @Mock
    private TaskHeadRepository mTaskHeadRepository;
    @Mock
    private TaskRepository mTaskRepository;

    @Captor
    private ArgumentCaptor<TaskHeadDataSource.GetTaskHeadCallback> mGetTaskHeadCallbackCaptor;
    @Captor
    private ArgumentCaptor<TaskDataSource.LoadTasksCallback> mLoadTasksCallbackCaptor;

    @Before
    public void setup() {

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getTaskHeadFromRepo_withValidTaskHeadId() {
        // Given the tasksPresenter with valid taskheadId
        mTasksPresenter = givenTasksPresenter(TASKHEAD.getId());

        // When the presenter is asked to populate an existing taskhead
        mTasksPresenter.populateTaskHead();
        // Then the taskhead repo is queried
        verify(mTaskHeadRepository).getTaskHead(eq(TASKHEAD.getId()), mGetTaskHeadCallbackCaptor.capture());
        mGetTaskHeadCallbackCaptor.getValue().onTaskHeadLoaded(TASKHEAD);
        // and update view related TaskHead
        verify(mTasksView).setTitle(TASKHEAD.getTitle());
        verify(mTasksView).setMembers(TASKHEAD.getMembers());
    }

    @Test
    public void getTasksFromRepo_withTaskHeadIdAndMemberId_andUpdateView() {
        mTasksPresenter = givenTasksPresenter(TASKHEAD.getId());
        // Get tasks of selected member
        final String memberId = MEMBERS.get(0).getId();
        mTasksPresenter.getTasks(memberId);

        verify(mTaskRepository).getTasks(eq(TASKHEAD.getId()), eq(memberId), mLoadTasksCallbackCaptor.capture());
        mLoadTasksCallbackCaptor.getValue().onTasksLoaded(TASKS);

        ArgumentCaptor<List> showTasksArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(mTasksView).showTasks(showTasksArgumentCaptor.capture());
        assertTrue(showTasksArgumentCaptor.getValue().size() == TASKS.size());
    }

    @Test
    public void getTasksFromRepo_withTaskHeadId_updateView() {
        mTasksPresenter = givenTasksPresenter(TASKHEAD.getId());
        // Get tasks by taskHeadId
        mTasksPresenter.getTasks();

        verify(mTaskRepository).getTasks(eq(TASKHEAD.getId()), mLoadTasksCallbackCaptor.capture());
        mLoadTasksCallbackCaptor.getValue().onTasksLoaded(TASKS);

        ArgumentCaptor<List> showTasksArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(mTasksView).showTasks(showTasksArgumentCaptor.capture());
        assertTrue(showTasksArgumentCaptor.getValue().size() == TASKS.size());
    }
    @Test
    public void createTask_andLoadIntoView() {
        mTasksPresenter = givenTasksPresenter(TASKHEAD.getId());
        // Create a new taskTextView
        String memberId = TASKS.get(0).getMemberId();
        String description = "description new taskTextView";
        int order = TASKS.size();
        mTasksPresenter.createTask(memberId, description, order);
        // Then a taskTextView is saved in the repository
        verify(mTaskRepository).saveTask(any(Task.class));
        // And Update View
//        verify(mTasksView).showTasks();
    }

    @Test
    public void updateTask_editTask() {
        mTasksPresenter = givenTasksPresenter(TASKHEAD.getId());

        String memberId = TASKS.get(0).getMemberId();
        int order = TASKS.size();
        // Update a taskTextView
        mTasksPresenter.updateTask(memberId, "taskId", "changed description", order);
        verify(mTaskRepository).saveTask(any(Task.class));
    }

    @Test
    public void deleteTask() {
        mTasksPresenter = givenTasksPresenter(TASKHEAD.getId());
        Task task = TASKS.get(0);
        mTasksPresenter.deleteTask(task.getId());
        verify(mTaskRepository).deleteTask(task.getId());
    }

    private TasksPresenter givenTasksPresenter(String taskHeadId) {
        UseCaseHandler useCaseHandler = new UseCaseHandler(new TestUseCaseScheduler());
        GetTaskHead getTaskHead = new GetTaskHead(mTaskHeadRepository);
        GetTasksOfMember getTasksOfMember = new GetTasksOfMember(mTaskRepository);
        SaveTask saveTask = new SaveTask(mTaskRepository);
        DeleteTask deleteTask = new DeleteTask(mTaskRepository);
        GetTasksOfTaskHead getTasksOfTaskHead = new GetTasksOfTaskHead(mTaskRepository);

        return new TasksPresenter(useCaseHandler, taskHeadId, mTasksView,
                getTaskHead, getTasksOfMember, saveTask, deleteTask, getTasksOfTaskHead);
    }

}