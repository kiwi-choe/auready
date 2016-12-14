package com.kiwi.auready_ver2.taskheaddetail;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.TestUseCaseScheduler;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.source.TaskHeadDataSource;
import com.kiwi.auready_ver2.data.source.TaskHeadRepository;
import com.kiwi.auready_ver2.data.source.TaskRepository;
import com.kiwi.auready_ver2.taskheaddetail.domain.usecase.GetTaskHead;
import com.kiwi.auready_ver2.taskheaddetail.domain.usecase.SaveTaskHead;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of {@link TaskHeadDetailPresenter}.
 */
public class TaskHeadDetailPresenterTest {

    private static List<Friend> MEMBERS;

    @Mock
    private TaskHeadRepository mTaskHeadRepository;
    @Mock
    private TaskRepository mTasksRepository;

    @Mock
    private TaskHeadDetailContract.View mTaskHeadDetailView;

    private TaskHeadDetailPresenter mTaskHeadDetailPresenter;

    @Captor
    private ArgumentCaptor<TaskHeadDataSource.GetTaskHeadCallback> mGetTaskHeadCallbackCaptor;

    @Before
    public void setupMocksAndView() {
        MockitoAnnotations.initMocks(this);

        MEMBERS = Lists.newArrayList(new Friend("email1", "name1"), new Friend("email2", "name2"),
                new Friend("email3", "name3"));
    }

    @Test
    public void saveNewTaskHeadToRepo() {
        // taskheadId is null
        mTaskHeadDetailPresenter = givenTaskHeadDetailPresenter(null);
        // When the presenter is asked to save a taskhead
        mTaskHeadDetailPresenter.saveTaskHead("New Title", MEMBERS);

        // Then a taskhead is saved in the repository
        verify(mTaskHeadRepository).saveTaskHead(any(TaskHead.class));
        // and the view updated.
        // TODO: 12/9/16 need a TaskHeadId instead of any(String)
        verify(mTaskHeadDetailView).setResultToTaskHeadsView(any(String.class));
    }

    @Test
    public void saveTaskHead_emptyTaskHeadShowsErrorUI() {
        mTaskHeadDetailPresenter = givenTaskHeadDetailPresenter(null);
        mTaskHeadDetailPresenter.saveTaskHead("", null);

        verify(mTaskHeadDetailView).showEmptyTaskHeadError();
    }
    @Test
    public void saveExistingTaskHead_editTitle() {
        mTaskHeadDetailPresenter = givenTaskHeadDetailPresenter("taskHeadId_mock");

        // Modify title and ask to save
        mTaskHeadDetailPresenter.saveTaskHead("changed title", MEMBERS);

        // Then a taskhead is saved in the repo and the view updated
        verify(mTaskHeadRepository).saveTaskHead(any(TaskHead.class));
        verify(mTaskHeadDetailView).setResultToTaskHeadsView(any(String.class));
    }

    @Test
    public void getTaskHeadFromRepo_callsRepoAndUpdatesView() {

        TaskHead taskHead = new TaskHead("title1", MEMBERS);
        mTaskHeadDetailPresenter = givenTaskHeadDetailPresenter(taskHead.getId());

        // When the presenter is asked to populate an existing taskhead
        mTaskHeadDetailPresenter.populateTaskHead();
        // Then the taskhead repository is queried
        verify(mTaskHeadRepository).getTaskHead(eq(taskHead.getId()), mGetTaskHeadCallbackCaptor.capture());
        mGetTaskHeadCallbackCaptor.getValue().onTaskHeadLoaded(taskHead);
        // and update view
        verify(mTaskHeadDetailView).setTitle(taskHead.getTitle());
        verify(mTaskHeadDetailView).setMembers(taskHead.getMembers());
    }

    private TaskHeadDetailPresenter givenTaskHeadDetailPresenter(String taskHeadId) {
        UseCaseHandler useCaseHandler = new UseCaseHandler(new TestUseCaseScheduler());
        SaveTaskHead saveTaskHead = new SaveTaskHead(mTaskHeadRepository);
        GetTaskHead getTaskHead = new GetTaskHead(mTaskHeadRepository);

        return new TaskHeadDetailPresenter(useCaseHandler, taskHeadId, mTaskHeadDetailView, saveTaskHead, getTaskHead);

    }
}