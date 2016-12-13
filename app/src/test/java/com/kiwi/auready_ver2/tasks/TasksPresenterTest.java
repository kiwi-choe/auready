package com.kiwi.auready_ver2.tasks;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.TestUseCaseScheduler;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.source.TaskDataSource;
import com.kiwi.auready_ver2.data.source.TaskHeadDataSource;
import com.kiwi.auready_ver2.data.source.TaskHeadRepository;
import com.kiwi.auready_ver2.data.source.TaskRepository;
import com.kiwi.auready_ver2.taskheaddetail.domain.usecase.GetTaskHead;
import com.kiwi.auready_ver2.tasks.domain.usecase.GetTasks;
import com.kiwi.auready_ver2.tasks.domain.usecase.SaveTask;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of {@link TasksPresenter}.
 */
public class TasksPresenterTest {

    private static final String INVALID_TASKHEAD_ID = "";
    private static final String TASK_DESCRIPTION1 = "someday";
    private static final String TASK_DESCRIPTION2 = "we will know";
    private static final String TASK_DESCRIPTION3 = "OK?";

    /*
    * {@link Task}s stub that is added to the fake service API layer.
    * */
    // 3 tasks, one active and two completed of MEMBER the index 0
    private static final List<Friend> MEMBERS = Lists.newArrayList(new Friend("email1", "name1"), new Friend("email2", "name2"),
            new Friend("email3", "name3"));
    private static TaskHead TASKHEAD = new TaskHead("title1", MEMBERS);

    private static List<Task> TASKS = Lists.newArrayList(
            new Task(TASKHEAD.getId(), MEMBERS.get(0).getId(), TASK_DESCRIPTION1, 0),
            new Task(TASKHEAD.getId(), MEMBERS.get(0).getId(), TASK_DESCRIPTION2, true, 0),
            new Task(TASKHEAD.getId(), MEMBERS.get(0).getId(), TASK_DESCRIPTION3, true, 0));

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
    private ArgumentCaptor<TaskDataSource.GetTasksCallback> mLoadTasksCallbackCaptor;

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

    // test when start, populate if the taskheadId exists

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
    public void saveTask() {
        mTasksPresenter = givenTasksPresenter(TASKHEAD.getId());
        // Create a new task
        String memberId = TASKS.get(0).getMemberId();
        String description = "description new task";
        int order = TASKS.size();
        mTasksPresenter.saveTask(memberId, description, order);
        // Then a task is saved in the repository
        verify(mTaskRepository).saveTask(any(Task.class));
    }

    @Test
    public void updateTask() {
        mTasksPresenter = givenTasksPresenter(TASKHEAD.getId());
        // Update a task
    }

    private TasksPresenter givenTasksPresenter(String taskHeadId) {
        UseCaseHandler useCaseHandler = new UseCaseHandler(new TestUseCaseScheduler());
        GetTaskHead getTaskHead = new GetTaskHead(mTaskHeadRepository);
        GetTasks getTasks = new GetTasks(mTaskRepository);
        SaveTask saveTask = new SaveTask(mTaskRepository);

        return new TasksPresenter(useCaseHandler, taskHeadId, mTasksView,
                getTaskHead, getTasks, saveTask);
    }

}