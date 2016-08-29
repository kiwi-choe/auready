package com.kiwi.auready_ver2.tasks;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.TestUseCaseScheduler;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.source.TaskDataSource;
import com.kiwi.auready_ver2.data.source.TaskHeadRepository;
import com.kiwi.auready_ver2.data.source.TaskRepository;
import com.kiwi.auready_ver2.tasks.domain.usecase.GetTasks;
import com.kiwi.auready_ver2.tasks.domain.usecase.SaveTaskHead;
import com.kiwi.auready_ver2.tasks.domain.usecase.SaveTasks;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
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
    private TaskHeadRepository mTaskHeadRepository;
    @Mock
    private TaskRepository mTaskRepository;
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
        GetTasks getTasks = new GetTasks(mTaskRepository);
        SaveTasks saveTasks = new SaveTasks(mTaskRepository);
        SaveTaskHead saveTaskHead = new SaveTaskHead(mTaskHeadRepository);
        return new TasksPresenter(useCaseHandler, taskHeadId, mTasksView, getTasks, saveTasks, saveTaskHead);
    }

    @Test
    public void startTasksPresenter_saveNewTaskHead() {
        mTasksPresenter = givenTasksPresenter(null);

        // To make taskHead id
        mTasksPresenter.start();

        // Given a stubbed new taskHead
        verify(mTaskHeadRepository).saveTaskHead(any(TaskHead.class));
    }

    @Test
    public void saveTask_emptyTaskAndNoTitleShowsErrorUi() {

        // When TaskHeadId is null,
        mTasksPresenter = givenTasksPresenter(null);

        String title = "";
        List<Task> tasks = new ArrayList<>(0);
        mTasksPresenter.saveTaskHead(title, tasks);

        boolean isEmpty = mTasksPresenter.isEmptyTaskHead(title, tasks);
        Assert.assertTrue(isEmpty);

        verify(mTasksView).showEmptyTasksError();
    }

    @Test
    public void saveNewTaskHeadToRepository_showsSuccessMessageUi() {
        // no taskHead id
        mTasksPresenter = givenTasksPresenter(null);


        String title = "title1";
        List<Task> tasks = Lists.newArrayList(new Task("description1"), new Task("description2"));
        mTasksPresenter.saveTaskHead(title, tasks);

    }
}