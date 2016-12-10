package com.kiwi.auready_ver2.tasks;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.TestUseCaseScheduler;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.source.TaskDataSource;
import com.kiwi.auready_ver2.data.source.TaskRepository;
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

    private static final List<String> MEMBERS = Lists.newArrayList("mem1", "mem2", "mem3");
    private static TaskHead TASKHEAD = new TaskHead("title1", MEMBERS);

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

    @Test
    public void loadTaskHead_withValidTaskHeadId() {
        // When TasksPresenter is asked to open a taskhead
        mTasksPresenter = givenTasksPresenter(TASKHEAD.getId());

        //
    }
    private TasksPresenter givenTasksPresenter(String taskHeadId) {
        UseCaseHandler useCaseHandler = new UseCaseHandler(new TestUseCaseScheduler());
        GetTasks getTasks = new GetTasks(mTaskRepository);
        SaveTask saveTask = new SaveTask(mTaskRepository);
        CompleteTask completeTask = new CompleteTask(mTaskRepository);
        ActivateTask activateTask = new ActivateTask(mTaskRepository);
        SortTasks sortTasks = new SortTasks(mTaskRepository);
        DeleteTask deleteTask = new DeleteTask(mTaskRepository);
        EditDescription editDescription = new EditDescription(mTaskRepository);

        return new TasksPresenter(useCaseHandler, taskHeadId, mTasksView,
                getTasks, saveTask, completeTask, activateTask, sortTasks, deleteTask, editDescription);
    }

}