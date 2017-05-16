package com.kiwi.auready_ver2.tasks;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.TestUseCaseScheduler;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.data.source.TaskDataSource;
import com.kiwi.auready_ver2.data.source.TaskRepository;
import com.kiwi.auready_ver2.taskheaddetail.domain.usecase.GetTaskHeadDetail;
import com.kiwi.auready_ver2.tasks.domain.usecase.DeleteTask;
import com.kiwi.auready_ver2.tasks.domain.usecase.EditTasks;
import com.kiwi.auready_ver2.tasks.domain.usecase.EditTasksOfMember;
import com.kiwi.auready_ver2.tasks.domain.usecase.GetMembers;
import com.kiwi.auready_ver2.tasks.domain.usecase.GetTasksOfMember;
import com.kiwi.auready_ver2.tasks.domain.usecase.SaveTask;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static com.kiwi.auready_ver2.StubbedData.TaskStub.MEMBERS;
import static com.kiwi.auready_ver2.StubbedData.TaskStub.TASKHEAD;
import static com.kiwi.auready_ver2.StubbedData.TaskStub.TASKHEAD_DETAIL;
import static com.kiwi.auready_ver2.StubbedData.TaskStub.TASKS;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
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
    private TaskRepository mTaskRepository;

    @Mock
    private TaskDataSource.LoadMembersCallback mLoadMembersCallback;
    @Captor
    private ArgumentCaptor<TaskDataSource.LoadMembersCallback> mLoadMembersCallbackCaptor;
    @Captor
    private ArgumentCaptor<TaskDataSource.LoadTasksCallback> mLoadTasksCallbackCaptor;
    @Captor
    private ArgumentCaptor<TaskDataSource.GetTaskHeadDetailCallback> getTaskHeadDetailCallbackCaptor;

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
    public void getMembers_whenStart() {
        mTasksPresenter = givenTasksPresenter(TASKHEAD.getId());
        mTasksPresenter.start();

        verify(mTaskRepository).getMembers(eq(TASKHEAD.getId()), mLoadMembersCallbackCaptor.capture());
        mLoadMembersCallbackCaptor.getValue().onMembersLoaded(MEMBERS);
        verify(mTasksView).showMembers(MEMBERS);
    }

    @Test
    public void cannotGetMembersAndView_withInvalidTaskHeadId_whenStartPresenter() {
        mTasksPresenter = givenTasksPresenter(null);

        mTasksPresenter.start();

        verify(mTaskRepository, never()).getMembers(anyString(), mLoadMembersCallbackCaptor.capture());
        verify(mTasksView, never()).showMembers(anyList());
    }

    @Test
    public void getTasksOfMember() {
        mTasksPresenter = givenTasksPresenter(TASKHEAD.getId());

        // Get tasks of selected member
        final String memberId = MEMBERS.get(0).getId();
        mTasksPresenter.getTasksOfMember(memberId);

        verify(mTaskRepository).getTasksOfMember(eq(memberId), mLoadTasksCallbackCaptor.capture());
        mLoadTasksCallbackCaptor.getValue().onTasksLoaded(TASKS);
    }

    @Test
    public void filterCompletedTasks_AfterLoadingTasks_AndUpdateView() {
        mTasksPresenter = givenTasksPresenter(TASKHEAD.getId());

        // Given TASKS: 2 completed tasks, 1 uncompleted task
        // Get tasks of selected member
        final String memberId = MEMBERS.get(0).getId();
        mTasksPresenter.getTasksOfMember(memberId);

        // Filter completed tasks and others
        List<Task> completed = new ArrayList<>();
        List<Task> uncompleted = new ArrayList<>();
        mTasksPresenter.filterTasks(TASKS, completed, uncompleted);
        // Verify that filtering result
        assertThat(completed.size(), is(2));
        assertThat(uncompleted.size(), is(1));

        verify(mTasksView).showTasks(memberId, completed, uncompleted);
    }
    @Test
    public void getTasksOfMember_whenTasksIsEmpty() {
        mTasksPresenter = givenTasksPresenter(TASKHEAD.getId());

        // Get tasks of selected member
        final String memberId = MEMBERS.get(0).getId();
        mTasksPresenter.getTasksOfMember(memberId);

        verify(mTaskRepository).getTasksOfMember(eq(memberId), mLoadTasksCallbackCaptor.capture());
        mLoadTasksCallbackCaptor.getValue().onDataNotAvailable();
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
    }

    @Test
    public void editTasks() {
        mTasksPresenter = givenTasksPresenter(TASKHEAD.getId());

        // Update a taskTextView
        String taskheadid = TASKHEAD.getId();
        mTasksPresenter.editTasks();
//        verify(mTaskRepository).editTasks(eq(taskheadid), eq(TASKS));
    }

    @Test
    public void deleteTasks() {
        mTasksPresenter = givenTasksPresenter(TASKHEAD.getId());

        String taskId = TASKS.get(0).getId();
        mTasksPresenter.deleteTask(TASKS.get(0).getMemberId(), taskId);
        verify(mTaskRepository).deleteTask(eq(taskId));
    }

    @Test
    public void saveEditedTasksInMemory() {
        mTasksPresenter = givenTasksPresenter(TASKHEAD.getId());

        String memberId = "stubbed_memberId";
        mTasksPresenter.updateTasksInMemory(memberId, TASKS);

        assertThat(mTasksPresenter.mCachedTasks.containsKey(memberId), is(true));
        assertThat(mTasksPresenter.mCachedTasks.get(memberId), is(TASKS));

        // Modify the existing value test
        List<Task> editedTasks = Lists.newArrayList(
                new Task("stubbedTask0", MEMBERS.get(0).getId(), "editedDescription!!!!!", 0));
        mTasksPresenter.updateTasksInMemory(memberId, editedTasks);
        assertEquals(mTasksPresenter.mCachedTasks.size(), 1);
        assertThat(mTasksPresenter.mCachedTasks.get(memberId), is(not(TASKS)));
        assertThat(mTasksPresenter.mCachedTasks.get(memberId), is(editedTasks));
    }
    @Test
    public void getTaskHeadDetailFromRemote() {
        mTasksPresenter = givenTasksPresenter(TASKHEAD.getId());

        mTasksPresenter.getTaskHeadDetailFromRemote();
        verify(mTaskRepository).getTaskHeadDetail(eq(TASKHEAD.getId()), getTaskHeadDetailCallbackCaptor.capture());
        getTaskHeadDetailCallbackCaptor.getValue().onTaskHeadDetailLoaded(TASKHEAD_DETAIL);

        verify(mTasksView).setTitle(TASKHEAD_DETAIL.getTaskHead().getTitle());
        verify(mTasksView).setColor(TASKHEAD_DETAIL.getTaskHead().getColor());
        verify(mTasksView).showMembers(TASKHEAD_DETAIL.getMembers());
    }
    private TasksPresenter givenTasksPresenter(String taskHeadId) {

        UseCaseHandler useCaseHandler = new UseCaseHandler(new TestUseCaseScheduler());
        GetMembers getMembers = new GetMembers(mTaskRepository);
        GetTasksOfMember getTasksOfMember = new GetTasksOfMember(mTaskRepository);
        SaveTask saveTask = new SaveTask(mTaskRepository);
        DeleteTask deleteTask = new DeleteTask(mTaskRepository);
        EditTasks editTasks = new EditTasks(mTaskRepository);
        EditTasksOfMember editTasksOfMember = new EditTasksOfMember(mTaskRepository);
        GetTaskHeadDetail getTaskHeadDetail = new GetTaskHeadDetail(mTaskRepository);

        return new TasksPresenter(useCaseHandler, taskHeadId, mTasksView,
                getMembers, getTasksOfMember, saveTask, deleteTask, editTasks, editTasksOfMember,
                getTaskHeadDetail);
    }

}