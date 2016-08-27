package com.kiwi.auready_ver2.tasks;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.TestUseCaseScheduler;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.source.TaskDataSource;
import com.kiwi.auready_ver2.data.source.TaskRepository;
import com.kiwi.auready_ver2.tasks.domain.usecase.GetTasks;
import com.kiwi.auready_ver2.tasks.domain.usecase.SaveTasks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the implementation of {@link TasksPresenter}.
 */
public class TasksPresenterTest {

    private static List<Task> TASKS;

    private TasksPresenter mTasksPresenter;

    @Mock
    private TasksContract.View mTasksView;
    @Mock
    private TaskRepository mTasksRepository;
    @Captor
    private ArgumentCaptor<TaskDataSource.LoadTasksCallback> mLoadTasksCallbackCaptor;

    @Before
    public void setup() {

        MockitoAnnotations.initMocks(this);

        TASKS = Lists.newArrayList(new Task("taskheadId1", "description1"),
                new Task("taskheadId2", "description2"), new Task("taskheadId3", "description3"));

        // The presenter won't update the view unless it's active
        when(mTasksView.isActive()).thenReturn(true);
    }

    private TasksPresenter givenTasksPresenter(String taskHeadId) {
        UseCaseHandler useCaseHandler = new UseCaseHandler(new TestUseCaseScheduler());
        GetTasks getTasks = new GetTasks(mTasksRepository);
        SaveTasks saveTasks = new SaveTasks(mTasksRepository);

        return new TasksPresenter(useCaseHandler, taskHeadId, mTasksView, getTasks, saveTasks);
    }

    @Test
    public void addNewTaskHeadsWithNewTasks() {

        mTasksPresenter = givenTasksPresenter(null);


        mTasksPresenter.loadTasks();
    }
    @Test
    public void loadTasksFromRepository_andLoadIntoView() {

//        TASKHEAD = new
//        TASKS = new Task("description1");

        mTasksPresenter.loadTasks();

        verify(mTasksRepository).getTasks(mLoadTasksCallbackCaptor.capture());
        mLoadTasksCallbackCaptor.getValue().onTasksLoaded(TASKS);

        ArgumentCaptor<List> showTaskArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(mTasksView).showTasks(showTaskArgumentCaptor.capture());
        assertTrue(showTaskArgumentCaptor.getValue().size() == 3);

    }
}