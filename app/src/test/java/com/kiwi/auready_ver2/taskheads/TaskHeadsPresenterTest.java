package com.kiwi.auready_ver2.taskheads;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.TestUseCaseScheduler;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.source.TaskDataSource;
import com.kiwi.auready_ver2.data.source.TaskHeadDataSource.LoadTaskHeadsCallback;
import com.kiwi.auready_ver2.data.source.TaskHeadRepository;
import com.kiwi.auready_ver2.data.source.TaskRepository;
import com.kiwi.auready_ver2.taskheads.domain.usecase.DeleteTaskHead;
import com.kiwi.auready_ver2.taskheads.domain.usecase.GetTaskHeads;
import com.kiwi.auready_ver2.taskheads.domain.usecase.GetTaskHeadsCount;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Created by kiwi on 8/23/16.
 */
public class TaskHeadsPresenterTest {

    private static final String TASKHEAD_ID = "stubTaskHeadId";
    private static final String TITLE = "stubTitle";
    private static final List<Friend> MEMBERS = Lists.newArrayList(new Friend("email1", "name1"), new Friend("email2", "name2"),
            new Friend("email3", "name3"));

    private static List<TaskHead> TASKHEADS;

    private TaskHeadsPresenter mTaskHeadsPresenter;

    @Mock
    private TaskHeadRepository mTaskHeadRepository;
    @Mock
    private TaskRepository mTaskRepository;

    @Mock
    private TaskHeadsContract.View mTaskHeadView;

    @Captor
    private ArgumentCaptor<LoadTaskHeadsCallback> mLoadTaskHeadsCallbackCaptor;
    @Captor
    private ArgumentCaptor<TaskDataSource.DeleteTasksCallback> mDeleteTasksCallbackCaptor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mTaskHeadsPresenter = givenTaskHeadsPresenter();

        // Start 3 taskHeads with title and only a member.
        TASKHEADS = Lists.newArrayList(new TaskHead("title1", MEMBERS),
                new TaskHead("title2", MEMBERS), new TaskHead("title3", MEMBERS));
    }

    private TaskHeadsPresenter givenTaskHeadsPresenter() {

        UseCaseHandler useCaseHandler = new UseCaseHandler(new TestUseCaseScheduler());
        GetTaskHeads getTaskHeads = new GetTaskHeads(mTaskHeadRepository);
        DeleteTaskHead deleteTaskHead = new DeleteTaskHead(mTaskHeadRepository, mTaskRepository);
        GetTaskHeadsCount getTaskHeadsCount = new GetTaskHeadsCount(mTaskHeadRepository);

        return new TaskHeadsPresenter(useCaseHandler, mTaskHeadView,
                getTaskHeads, deleteTaskHead, getTaskHeadsCount);
    }

    @Test
    public void loadAllTaskHeadsFromRepository_andLoadIntoView() {
        mTaskHeadsPresenter.loadTaskHeads();

        verify(mTaskHeadRepository).getTaskHeads(mLoadTaskHeadsCallbackCaptor.capture());
        mLoadTaskHeadsCallbackCaptor.getValue().onTaskHeadsLoaded(TASKHEADS);

        ArgumentCaptor<List> showTaskHeadsArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(mTaskHeadView).showTaskHeads(showTaskHeadsArgumentCaptor.capture());
        assertTrue(showTaskHeadsArgumentCaptor.getValue().size() == TASKHEADS.size());
    }

    @Test
    public void deleteTaskHead_andLoadIntoView() {
        // Given an stubbed taskHead
        TaskHead taskHead = new TaskHead(TITLE, MEMBERS);
        mTaskHeadsPresenter.deleteTaskHead(taskHead.getId());

        // Verify deleteTasks is called
        verify(mTaskRepository).deleteTasks(eq(taskHead.getId()));
        // and deleteTaskHead is called
        verify(mTaskHeadRepository).deleteTaskHead(taskHead.getId());
    }

    @Test
    public void getTaskHeadsCountFromRepo_andShowsAddTaskHeadUi_whenCall_addNewTask() {
        mTaskHeadsPresenter.addNewTaskHead();

        verify(mTaskHeadRepository).getTaskHeadsCount();
        verify(mTaskHeadView).showTaskHeadDetail(anyInt());
    }
}