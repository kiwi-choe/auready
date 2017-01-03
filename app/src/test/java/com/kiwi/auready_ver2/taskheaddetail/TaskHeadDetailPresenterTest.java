package com.kiwi.auready_ver2.taskheaddetail;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.TestUseCaseScheduler;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.data.Member;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.TaskHeadDetail;
import com.kiwi.auready_ver2.data.source.TaskHeadDetailDataSource;
import com.kiwi.auready_ver2.data.source.TaskHeadDetailRepository;
import com.kiwi.auready_ver2.taskheaddetail.domain.usecase.GetTaskHeadDetail;
import com.kiwi.auready_ver2.taskheaddetail.domain.usecase.SaveTaskHeadDetail;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static com.kiwi.auready_ver2.StubbedData.TaskStub.MEMBERS;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of {@link TaskHeadDetailPresenter}.
 */
public class TaskHeadDetailPresenterTest {

    private static final String MY_ID_OF_FRIEND = "stubbedId";

    @Mock
    private TaskHeadDetailRepository mRepository;
    @Mock
    private TaskHeadDetailContract.View mView;

    private TaskHeadDetailPresenter mPresenter;

    @Captor
    private ArgumentCaptor<TaskHeadDetailDataSource.GetTaskHeadDetailCallback> mGetTaskHeadDetailCallbackCaptor;
    @Captor
    private ArgumentCaptor<TaskHeadDetailDataSource.SaveCallback> mSaveTaskHeadCallbackCaptor;

    @Before
    public void setupMocksAndView() {
        MockitoAnnotations.initMocks(this);

    }

    @Test
    public void createTaskHead_toRepo() {
        // taskheadId is null
        mPresenter = givenTaskHeadDetailPresenter(null);
        // When the presenter is asked to create a new taskHead coz of no taskheadId
        String title = "New Title";
        mPresenter.createTaskHeadDetail(title, MEMBERS, 0);

        // Then a taskhead is saved in the repository
        verify(mRepository).saveTaskHeadDetail(any(TaskHeadDetail.class), mSaveTaskHeadCallbackCaptor.capture());
        mSaveTaskHeadCallbackCaptor.getValue().onSaveSuccess();
        // and the view updated.
        // TODO: 12/9/16 need a TaskHeadId instead of any(String)
        verify(mView).showAddedTaskHead(any(String.class), eq(title));
    }

    @Test
    public void createTaskHead_emptyTaskHeadShowsErrorUI() {
        mPresenter = givenTaskHeadDetailPresenter(null);
        mPresenter.createTaskHeadDetail("", null, 0);

        verify(mView).showEmptyTaskHeadError();
    }

//    @Test
//    public void editTaskHead_editTitle() {
//        TaskHead taskHead = new TaskHead("title", MEMBERS, 0);
//        mPresenter = givenTaskHeadDetailPresenter(taskHead.getTaskHeadId());
//
//        // Modify title and update
//        String editTitle = "changed title";
//        mPresenter.editTaskHead(editTitle, MEMBERS);
//
//        // Then a taskhead is updated from repo and the view updated
//        verify(mTaskHeadRepository).editTaskHead(eq(taskHead.getTaskHeadId()), eq(editTitle), eq(MEMBERS));
//        verify(mView).showEditedTaskHead();
//    }

    @Test
    public void getMembersFromRepo_callsRepoAndUpdatesView() {

        TaskHead taskHead = new TaskHead("title", 0);
        ArrayList<Member> MEMBERS = Lists.newArrayList(
                new Member(taskHead.getId(), "email1", "name1"),
                new Member(taskHead.getId(), "email2", "name2"),
                new Member(taskHead.getId(), "email3", "name3"));
        mPresenter = givenTaskHeadDetailPresenter(taskHead.getId());

        // When the presenter is asked to populate an existing taskhead
        mPresenter.populateTaskHeadDetail();
        // Then the taskhead repository is queried
        verify(mRepository).getTaskHeadDetail(eq(taskHead.getId()), mGetTaskHeadDetailCallbackCaptor.capture());
        TaskHeadDetail taskHeadDetail = new TaskHeadDetail(taskHead, MEMBERS);
        mGetTaskHeadDetailCallbackCaptor.getValue().onTaskHeadDetailLoaded(taskHeadDetail);
        // and update view
        verify(mView).setTitle(taskHeadDetail.getTaskHead().getTitle());
        verify(mView).setMembers(taskHeadDetail.getMembers());
    }

    @Test
    public void whenStart_cannotPopulateTaskHeadDetail_withNull() {

        mPresenter = givenTaskHeadDetailPresenter(null);
        mPresenter.start();
        verify(mRepository, never()).getTaskHeadDetail(eq("taskHeadId"), mGetTaskHeadDetailCallbackCaptor.capture());
    }

    private TaskHeadDetailPresenter givenTaskHeadDetailPresenter(String taskHeadId) {
        UseCaseHandler useCaseHandler = new UseCaseHandler(new TestUseCaseScheduler());
        SaveTaskHeadDetail saveTaskHeadDetail = new SaveTaskHeadDetail(mRepository);
        GetTaskHeadDetail getTaskHeadDetail = new GetTaskHeadDetail(mRepository);

        return new TaskHeadDetailPresenter(useCaseHandler, taskHeadId, mView,
                saveTaskHeadDetail, getTaskHeadDetail);

    }
}