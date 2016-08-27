package com.kiwi.auready_ver2.taskheads;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.TestUseCaseScheduler;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.source.TaskHeadDataSource.LoadTaskHeadsCallback;
import com.kiwi.auready_ver2.data.source.TaskHeadRepository;
import com.kiwi.auready_ver2.taskheads.domain.usecase.GetTaskHeads;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;

/**
 * Created by kiwi on 8/23/16.
 */
public class TaskHeadPresenterTest {

    private static List<TaskHead> TASKHEADS;

    private TaskHeadPresenter mTaskHeadPresenter;

    @Mock
    private TaskHeadRepository mTasksRepository;
    @Mock
    private TaskHeadContract.View mTaskHeadsView;

    @Captor
    private ArgumentCaptor<LoadTaskHeadsCallback> mLoadTasksCallbackCaptor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mTaskHeadPresenter = givenTasksPresenter();

        // Start the taskHeads to 3.
        TASKHEADS = Lists.newArrayList(new TaskHead("title1"),
                new TaskHead("title2"), new TaskHead("title3"));
    }

    private TaskHeadPresenter givenTasksPresenter() {

        UseCaseHandler useCaseHandler = new UseCaseHandler(new TestUseCaseScheduler());
        GetTaskHeads getTaskHeads = new GetTaskHeads(mTasksRepository);

        return new TaskHeadPresenter(useCaseHandler, mTaskHeadsView, getTaskHeads);
    }

    @Test
    public void loadAllTaskHeadsFromRepository_andLoadIntoView() {
        mTaskHeadPresenter.loadTaskHeads();

        verify(mTasksRepository).getTaskHeads(mLoadTasksCallbackCaptor.capture());
        mLoadTasksCallbackCaptor.getValue().onTaskHeadsLoaded(TASKHEADS);

        ArgumentCaptor<List> showTaskHeadsArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(mTaskHeadsView).showTaskHeads(showTaskHeadsArgumentCaptor.capture());
        assertTrue(showTaskHeadsArgumentCaptor.getValue().size() == 3);
    }
}