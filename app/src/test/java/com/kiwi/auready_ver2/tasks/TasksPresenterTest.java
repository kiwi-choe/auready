package com.kiwi.auready_ver2.tasks;

import com.kiwi.auready_ver2.TestUseCaseScheduler;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.data.source.TaskDataSource;
import com.kiwi.auready_ver2.data.source.TaskHeadRepository;
import com.kiwi.auready_ver2.data.source.TaskRepository;
import com.kiwi.auready_ver2.tasks.domain.usecase.DeleteTask;
import com.kiwi.auready_ver2.tasks.domain.usecase.GetMembers;
import com.kiwi.auready_ver2.tasks.domain.usecase.GetTasksOfMember;
import com.kiwi.auready_ver2.tasks.domain.usecase.SaveTask;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.kiwi.auready_ver2.StubbedData.TaskStub.MEMBERS;
import static com.kiwi.auready_ver2.StubbedData.TaskStub.TASKHEAD;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
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

    @Mock
    private TaskDataSource.LoadMembersCallback mLoadMembersCallback;
    @Captor
    private ArgumentCaptor<TaskDataSource.LoadMembersCallback> mLoadMembersCallbackCaptor;

    @Before
    public void setup() {

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getMembers() {
        mTasksPresenter = givenTasksPresenter(TASKHEAD.getId());

        mTasksPresenter.populateMembers();

        // Get members from repo
        verify(mTaskRepository).getMembers(eq(TASKHEAD.getId()), mLoadMembersCallbackCaptor.capture());
        mLoadMembersCallbackCaptor.getValue().onMembersLoaded(MEMBERS);
        // and update View
        verify(mTasksView).showMembers(eq(MEMBERS));
    }

    @Test
    public void cannotGetMembers_withInvalidTaskHeadId_whenStartPresenter() {
        mTasksPresenter = givenTasksPresenter(null);

        mTasksPresenter.start();

        verify(mTaskRepository, never()).getMembers(anyString(), mLoadMembersCallbackCaptor.capture());
    }

//    @Test
//    public void getTaskHeadFromRepo_withValidTaskHeadId() {
//        // Given the tasksPresenter with valid taskheadId
//        mTasksPresenter = givenTasksPresenter(TASKHEAD.getTaskHeadId());
//
//        // When the presenter is asked to populate an existing taskhead
//        mTasksPresenter.populateTaskHeadDetail();
//        // Then the taskhead repo is queried
//        verify(mTaskHeadRepository).getTaskHeadDetail(eq(TASKHEAD.getTaskHeadId()), mGetTaskHeadCallbackCaptor.capture());
//        mGetTaskHeadCallbackCaptor.getValue().onTaskHeadDetailLoaded(TASKHEAD);
//        // and update view related TaskHead
//        verify(mTasksView).showTitle(TASKHEAD.getTitle());
//        verify(mTasksView).showMembers(TASKHEAD.getMembers());
//    }

//    @Test
//    public void getTasksFromRepo_withTaskHeadIdAndMemberId_andUpdateView() {
//        mTasksPresenter = givenTasksPresenter(TASKHEAD.getTaskHeadId());
//        // Get tasks of selected member
//        final String memberId = MEMBERS.get(0).getTaskHeadId();
//        mTasksPresenter.getTasks(memberId);
//
//        verify(mTaskRepository).getTasks(eq(TASKHEAD.getTaskHeadId()), eq(memberId), mLoadTasksCallbackCaptor.capture());
//        mLoadTasksCallbackCaptor.getValue().onTasksLoaded(TASKS);
//
//        ArgumentCaptor<List> showTasksArgumentCaptor = ArgumentCaptor.forClass(List.class);
//        verify(mTasksView).showTasks(showTasksArgumentCaptor.capture());
//        assertTrue(showTasksArgumentCaptor.getValue().size() == TASKS.size());
//    }
//
//    @Test
//    public void getTasksFromRepo_withTaskHeadId_updateView() {
//        mTasksPresenter = givenTasksPresenter(TASKHEAD.getTaskHeadId());
//        // Get tasks by taskHeadId
//        mTasksPresenter.getTasks();
//
//        verify(mTaskRepository).getTasks(eq(TASKHEAD.getTaskHeadId()), mLoadTasksCallbackCaptor.capture());
//        mLoadTasksCallbackCaptor.getValue().onTasksLoaded(TASKS);
//
//        ArgumentCaptor<List> showTasksArgumentCaptor = ArgumentCaptor.forClass(List.class);
//        verify(mTasksView).showTasks(showTasksArgumentCaptor.capture());
//        assertTrue(showTasksArgumentCaptor.getValue().size() == TASKS.size());
//    }
//
//    @Test
//    public void getTasks_whenStart() {
//        mTasksPresenter = givenTasksPresenter(TASKHEAD.getTaskHeadId());
//        mTasksPresenter.start();
//
//        verify(mTaskRepository).getTasks(eq(TASKHEAD.getTaskHeadId()), mLoadTasksCallbackCaptor.capture());
//        mLoadTasksCallbackCaptor.getValue().onTasksLoaded(TASKS);
//
//        ArgumentCaptor<List> showTasksArgumentCaptor = ArgumentCaptor.forClass(List.class);
//        verify(mTasksView).showTasks(showTasksArgumentCaptor.capture());
//        assertTrue(showTasksArgumentCaptor.getValue().size() == TASKS.size());
//    }
//    @Test
//    public void createTask_andLoadIntoView() {
//        mTasksPresenter = givenTasksPresenter(TASKHEAD.getTaskHeadId());
//        // Create a new taskTextView
//        String memberId = TASKS.get(0).getMemberId();
//        String description = "description new taskTextView";
//        int order = TASKS.size();
//        mTasksPresenter.createTask(memberId, description, order);
//        // Then a taskTextView is saved in the repository
//        verify(mTaskRepository).saveTask(any(Task.class));
//        // And Update View
////        verify(mTasksView).showTasks();
//    }
//
//    @Test
//    public void updateTask_editTask() {
//        mTasksPresenter = givenTasksPresenter(TASKHEAD.getTaskHeadId());
//
//        String memberId = TASKS.get(0).getMemberId();
//        int order = TASKS.size();
//        // Update a taskTextView
//        mTasksPresenter.updateTask(memberId, "taskId", "changed description", order);
//        verify(mTaskRepository).saveTask(any(Task.class));
//    }
//
//    @Test
//    public void deleteTask() {
//        mTasksPresenter = givenTasksPresenter(TASKHEAD.getTaskHeadId());
//        Task task = TASKS.get(0);
//        mTasksPresenter.deleteTask(task.getTaskHeadId());
//        verify(mTaskRepository).deleteTask(task.getTaskHeadId());
//    }

    private TasksPresenter givenTasksPresenter(String taskHeadId) {
        UseCaseHandler useCaseHandler = new UseCaseHandler(new TestUseCaseScheduler());
        GetTasksOfMember getTasksOfMember = new GetTasksOfMember(mTaskRepository);
        SaveTask saveTask = new SaveTask(mTaskRepository);
        DeleteTask deleteTask = new DeleteTask(mTaskRepository);
        GetMembers getMembers = new GetMembers(mTaskRepository);

        return new TasksPresenter(useCaseHandler, taskHeadId, mTasksView,
                getMembers);
    }

}