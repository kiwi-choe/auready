package com.kiwi.auready_ver2.taskheads;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.TestUseCaseScheduler;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.source.TaskHeadDataSource.LoadTaskHeadsCallback;
import com.kiwi.auready_ver2.data.source.TaskHeadRepository;
import com.kiwi.auready_ver2.taskheads.domain.usecase.DeleteTaskHead;
import com.kiwi.auready_ver2.taskheads.domain.usecase.GetTaskHeads;
import com.kiwi.auready_ver2.tasks.domain.usecase.SaveTaskHead;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

/**
 * Created by kiwi on 8/23/16.
 */
public class TaskHeadPresenterTest {

    private static List<TaskHead> TASKHEADS;

    private TaskHeadPresenter mTaskHeadPresenter;

    @Mock
    private TaskHeadRepository mTaskHeadRepository;
    @Mock
    private TaskHeadContract.View mTaskHeadView;

    @Captor
    private ArgumentCaptor<LoadTaskHeadsCallback> mLoadTaskHeadsCallbackCaptor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mTaskHeadPresenter = givenTaskHeadsPresenter();

        // Start the taskHeads to 3.
        TASKHEADS = Lists.newArrayList(new TaskHead("title1"),
                new TaskHead("title2"), new TaskHead("title3"));
    }

    private TaskHeadPresenter givenTaskHeadsPresenter() {

        UseCaseHandler useCaseHandler = new UseCaseHandler(new TestUseCaseScheduler());
        GetTaskHeads getTaskHeads = new GetTaskHeads(mTaskHeadRepository);
        DeleteTaskHead deleteTaskHead = new DeleteTaskHead(mTaskHeadRepository);
        SaveTaskHead saveTaskHead = new SaveTaskHead(mTaskHeadRepository);

        return new TaskHeadPresenter(useCaseHandler, mTaskHeadView, getTaskHeads, deleteTaskHead, saveTaskHead);
    }

    @Test
    public void loadAllTaskHeadsFromRepository_andLoadIntoView() {
        mTaskHeadPresenter.loadTaskHeads();

        verify(mTaskHeadRepository).getTaskHeads(mLoadTaskHeadsCallbackCaptor.capture());
        mLoadTaskHeadsCallbackCaptor.getValue().onTaskHeadsLoaded(TASKHEADS);

        ArgumentCaptor<List> showTaskHeadsArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(mTaskHeadView).showTaskHeads(showTaskHeadsArgumentCaptor.capture());
        assertTrue(showTaskHeadsArgumentCaptor.getValue().size() == 3);
    }

    @Test
    public void clickOnFab_showsAddTasksUi() {
        mTaskHeadPresenter.addNewTaskHead();

        verify(mTaskHeadView).openTasks(any(TaskHead.class));
    }

    @Test
    public void clickOnFab_createNewTaskHead() {
        mTaskHeadPresenter.addNewTaskHead();

        verify(mTaskHeadRepository).saveTaskHead(any(TaskHead.class));
    }

    @Test
    public void clickOnTaskHead_showsEditTasksUiWithTaskHeadId() {
        // Given a stubbed a taskHead
        TaskHead requestedTaskHead = new TaskHead("title1");
        mTaskHeadPresenter.editTasks(requestedTaskHead);
        verify(mTaskHeadView).openTasks(any(TaskHead.class));
    }

    @Test
    public void deleteTaskHead() {
        TaskHead taskHead = new TaskHead("title1");

        // When the deletion of a taskHead is requested,
        mTaskHeadPresenter.deleteTaskHead(taskHead.getId());
        // Then the repository and the view are notified.
        verify(mTaskHeadRepository).deleteTaskHead(taskHead.getId());
        verify(mTaskHeadView).showTaskHeadDeleted();
    }
}