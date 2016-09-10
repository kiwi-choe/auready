package com.kiwi.auready_ver2.taskheads;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.TestUseCaseScheduler;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.source.TaskHeadDataSource.LoadTaskHeadsCallback;
import com.kiwi.auready_ver2.data.source.TaskHeadRepository;
import com.kiwi.auready_ver2.taskheads.domain.usecase.DeleteTaskHead;
import com.kiwi.auready_ver2.taskheads.domain.usecase.GetTaskHeads;
import com.kiwi.auready_ver2.taskheads.domain.usecase.SaveTaskHead;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Created by kiwi on 8/23/16.
 */
public class TaskHeadsPresenterTest {

    private static final String TASKHEAD_ID = "stubTaskHeadId";
    private static final String TITLE = "stubTitle";

    private static List<TaskHead> TASKHEADS;

    private TaskHeadsPresenter mTaskHeadsPresenter;

    @Mock
    private TaskHeadRepository mTaskHeadRepository;
    @Mock
    private TaskHeadsContract.View mTaskHeadView;

    @Captor
    private ArgumentCaptor<LoadTaskHeadsCallback> mLoadTaskHeadsCallbackCaptor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mTaskHeadsPresenter = givenTaskHeadsPresenter();

        // Start the taskHeads to 3.
        TASKHEADS = Lists.newArrayList(new TaskHead("title1"),
                new TaskHead("title2"), new TaskHead("title3"));
    }

    private TaskHeadsPresenter givenTaskHeadsPresenter() {

        UseCaseHandler useCaseHandler = new UseCaseHandler(new TestUseCaseScheduler());
        GetTaskHeads getTaskHeads = new GetTaskHeads(mTaskHeadRepository);
        DeleteTaskHead deleteTaskHead = new DeleteTaskHead(mTaskHeadRepository);
        SaveTaskHead saveTaskHead = new SaveTaskHead(mTaskHeadRepository);

        return new TaskHeadsPresenter(useCaseHandler, mTaskHeadView, getTaskHeads, deleteTaskHead, saveTaskHead);
    }

    @Test
    public void loadAllTaskHeadsFromRepository_andLoadIntoView() {
        mTaskHeadsPresenter.loadTaskHeads();

        verify(mTaskHeadRepository).getTaskHeads(mLoadTaskHeadsCallbackCaptor.capture());
        mLoadTaskHeadsCallbackCaptor.getValue().onTaskHeadsLoaded(TASKHEADS);

        ArgumentCaptor<List> showTaskHeadsArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(mTaskHeadView).showTaskHeads(showTaskHeadsArgumentCaptor.capture());
        assertTrue(showTaskHeadsArgumentCaptor.getValue().size() == 3);
    }

    @Test
    public void saveNewTaskHeadToRepository_showAddTasksUi() {
        // Create new taskHead
        mTaskHeadsPresenter.saveTaskHead("", "");

        verify(mTaskHeadRepository).saveTaskHead(any(TaskHead.class));
        verify(mTaskHeadView).openTasks(any(TaskHead.class));
    }

    @Test
    public void updateTaskHeadToRepository() {
        mTaskHeadsPresenter.saveTaskHead(TASKHEAD_ID, TITLE);

        verify(mTaskHeadRepository).saveTaskHead(any(TaskHead.class));
    }

    @Test
    public void clickOnTaskHead_openTasksViewWithTaskHeadId() {
        // Given a stubbed a taskHead
        TaskHead requestedTaskHead = new TaskHead(TASKHEAD_ID, TITLE);

        mTaskHeadsPresenter.editTaskHead(requestedTaskHead);
        verify(mTaskHeadView).openTasks(any(TaskHead.class));
    }

    @Test
    public void deleteTaskHead() {

        TaskHead taskHead = new TaskHead("title1");
        // When the deletion of a taskHead is requested,
        mTaskHeadsPresenter.deleteTaskHead(taskHead.getId());

        // Then the repository are notified.
        verify(mTaskHeadRepository).deleteTaskHead(taskHead.getId());
    }

    @Test
    public void deleteTaskHeadByIsEmptyTaskHead() {
        TaskHead taskHead = new TaskHead();
        // When the deletion of a taskHead is requested,
        mTaskHeadsPresenter.deleteTaskHeadByIsEmptyTaskHead(taskHead.getId());

        // Then the repository and the view are notified.
        verify(mTaskHeadRepository).deleteTaskHead(taskHead.getId());
    }
}