package com.kiwi.auready_ver2.taskheaddetail;

import com.kiwi.auready_ver2.TestUseCaseScheduler;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.TaskHeadDetail;
import com.kiwi.auready_ver2.data.source.TaskDataSource;
import com.kiwi.auready_ver2.data.source.TaskRepository;
import com.kiwi.auready_ver2.taskheaddetail.domain.usecase.EditTaskHeadDetail;
import com.kiwi.auready_ver2.taskheaddetail.domain.usecase.GetTaskHeadDetail;
import com.kiwi.auready_ver2.taskheaddetail.domain.usecase.SaveTaskHeadDetail;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
    private TaskRepository mRepository;
    @Mock
    private TaskHeadDetailContract.View mView;

    private TaskHeadDetailPresenter mPresenter;
    @Captor
    private ArgumentCaptor<TaskDataSource.SaveCallback> mSaveCallbackCaptor;
    @Mock
    private TaskDataSource.EditTaskHeadDetailCallback mEditCallback;
    @Captor
    private ArgumentCaptor<TaskDataSource.EditTaskHeadDetailCallback> mEditCallbackCaptor;
    @Captor
    private ArgumentCaptor<TaskDataSource.GetTaskHeadDetailCallback> mGetCallbackCaptor;

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
        mPresenter.createTaskHeadDetail(title, 0, MEMBERS);

        // Then a taskhead is saved in the repository
        verify(mRepository).saveTaskHeadDetail(any(TaskHeadDetail.class), mSaveCallbackCaptor.capture());
        mSaveCallbackCaptor.getValue().onSaveSuccess();
        // and the view updated.
        // TODO: 12/9/16 need a TaskHeadId instead of any(String)
        verify(mView).showAddedTaskHead(any(String.class), eq(title));
    }

    @Test
    public void createTaskHead_emptyTaskHeadShowsErrorUI() {
        mPresenter = givenTaskHeadDetailPresenter(null);
        mPresenter.createTaskHeadDetail("", 0, null);

        verify(mView).showEmptyTaskHeadError();
    }

    @Test
    public void editTaskHeadDetail_toRepo() {
        TaskHead taskHead = new TaskHead("title", 0);
        mPresenter = givenTaskHeadDetailPresenter(taskHead.getId());

        mPresenter.editTaskHeadDetail("EDITTITLE", taskHead.getOrder(), MEMBERS);

        // Then a taskhead is updated from repo and the view updated
        verify(mRepository).editTaskHeadDetail(any(TaskHeadDetail.class), mEditCallbackCaptor.capture());
        mEditCallbackCaptor.getValue().onEditSuccess();
        verify(mView).showEditedTaskHead();
    }

    @Test
    public void getTaskHeadDetail_fromRepo() {

        TaskHead taskHead = new TaskHead("title", 0);
        mPresenter = givenTaskHeadDetailPresenter(taskHead.getId());

        // When the presenter is asked to populate an existing taskhead
        mPresenter.populateTaskHeadDetail();
        // Then the taskhead repository is queried
        verify(mRepository).getTaskHeadDetail(eq(taskHead.getId()), mGetCallbackCaptor.capture());
        TaskHeadDetail taskHeadDetail = new TaskHeadDetail(taskHead, MEMBERS);
        mGetCallbackCaptor.getValue().onTaskHeadDetailLoaded(taskHeadDetail);
        // and update view
        verify(mView).setTitle(taskHeadDetail.getTaskHead().getTitle());
        verify(mView).setMembers(taskHeadDetail.getMembers());
    }

    @Test
    public void whenStart_cannotPopulateTaskHeadDetail_withNull() {
        mPresenter = givenTaskHeadDetailPresenter(null);
        mPresenter.start();
        verify(mRepository, never()).getTaskHeadDetail(eq("taskHeadId"), mGetCallbackCaptor.capture());
    }

    private TaskHeadDetailPresenter givenTaskHeadDetailPresenter(String taskHeadId) {
        UseCaseHandler useCaseHandler = new UseCaseHandler(new TestUseCaseScheduler());
        SaveTaskHeadDetail saveTaskHeadDetail = new SaveTaskHeadDetail(mRepository);
        GetTaskHeadDetail getTaskHeadDetail = new GetTaskHeadDetail(mRepository);
        EditTaskHeadDetail editTaskHeadDetail = new EditTaskHeadDetail(mRepository);

        return new TaskHeadDetailPresenter(useCaseHandler, taskHeadId, mView,
                saveTaskHeadDetail, getTaskHeadDetail, editTaskHeadDetail);

    }
}