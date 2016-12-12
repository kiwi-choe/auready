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
import com.kiwi.auready_ver2.tasks.domain.usecase.ActivateTask;
import com.kiwi.auready_ver2.tasks.domain.usecase.CompleteTask;
import com.kiwi.auready_ver2.tasks.domain.usecase.DeleteTask;
import com.kiwi.auready_ver2.tasks.domain.usecase.EditDescription;
import com.kiwi.auready_ver2.tasks.domain.usecase.GetTasks;
import com.kiwi.auready_ver2.tasks.domain.usecase.SaveTask;
import com.kiwi.auready_ver2.tasks.domain.usecase.SortTasks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static junit.framework.Assert.assertTrue;
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
    private static List<Task> TASKS = Lists.newArrayList(new Task(TASKHEAD_ID, TASK_DESCRIPTION1, 0),
            new Task(TASKHEAD_ID, TASK_DESCRIPTION2, true, 0), new Task(TASKHEAD_ID, TASK_DESCRIPTION3, true, 0));

    private static final List<Friend> MEMBERS = Lists.newArrayList(new Friend("email1", "name1"), new Friend("email2", "name2"),
            new Friend("email3", "name3"));
    private static TaskHead TASKHEAD = new TaskHead("title1", MEMBERS);

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
    @Captor
    private ArgumentCaptor<TaskDataSource.SaveTaskCallback> mSaveTaskCallbackCaptor;

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

    private TasksPresenter givenTasksPresenter(String taskHeadId) {
        UseCaseHandler useCaseHandler = new UseCaseHandler(new TestUseCaseScheduler());
        GetTaskHead getTaskHead = new GetTaskHead(mTaskHeadRepository);

        return new TasksPresenter(useCaseHandler, taskHeadId, mTasksView,
                getTaskHead);
    }

}