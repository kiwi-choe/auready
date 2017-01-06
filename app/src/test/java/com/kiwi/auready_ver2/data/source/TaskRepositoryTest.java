package com.kiwi.auready_ver2.data.source;

import com.kiwi.auready_ver2.data.Member;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.TaskHeadDetail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static com.kiwi.auready_ver2.StubbedData.TaskStub.MEMBERS;
import static com.kiwi.auready_ver2.StubbedData.TaskStub.TASKHEADS;
import static com.kiwi.auready_ver2.StubbedData.TaskStub.TASKHEAD_DETAIL;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of the in-memory repository with cache.
 */
public class TaskRepositoryTest {

    private static final String TASKHEAD_ID = "stub_taskHeadId";
    private static final String MEMBER_ID = "stub_memberId";
    private static final String DESCRIPTION = "stub_description";

    private TaskRepository mRepository;

    @Mock
    private TaskDataSource mLocalDataSource;
    @Mock
    private TaskDataSource mTaskRemoteDataSource;
    @Mock
    private TaskDataSource.LoadTaskHeadsCallback mLoadTaskHeadsCallback;
    @Captor
    private ArgumentCaptor<TaskDataSource.LoadTaskHeadsCallback> mLoadTaskHeadsCallbackCaptor;
    @Mock
    private TaskDataSource.SaveCallback mSaveCallback;
    @Captor
    private ArgumentCaptor<TaskDataSource.SaveCallback> mSaveCallbackCaptor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mRepository = TaskRepository.getInstance(mLocalDataSource);
    }
   @After
    public void destroyRepositoryInstance() {
        TaskRepository.destroyInstance();
    }

    /*
    * Get taskHeads
    * */
    @Test
    public void getTaskHeadsWithLocalUnavailable() {
        mRepository.getTaskHeads(mLoadTaskHeadsCallback);
        // Local data source has no data available
        setTaskHeadsNotAvailable(mLocalDataSource);

        verify(mLoadTaskHeadsCallback).onDataNotAvailable();
    }

    @Test
    public void getTaskHeads_requestsTaskHeadsFromLocal() {
        mRepository.getTaskHeads(mLoadTaskHeadsCallback);

        setTaskHeadsAvailable(mLocalDataSource, TASKHEADS);

        // Then taskHeads are loaded from the local
        verify(mLocalDataSource).getTaskHeads(any(TaskDataSource.LoadTaskHeadsCallback.class));
        verify(mLoadTaskHeadsCallback).onTaskHeadsLoaded(TASKHEADS);
    }

    /*
    * Save taskHeadDetail
    * save taskhead and members
    * */
    @Test
    public void whenSaveTaskHeadAndMembersSucceed_toLocal_firesOnSaveSuccess() {
        mRepository.saveTaskHeadDetail(TASKHEAD_DETAIL, mSaveCallback);

        saveTaskHeadAndMembersAreSucceed(mLocalDataSource, TASKHEAD_DETAIL);

        verify(mSaveCallback).onSaveSuccess();
    }
    @Test
    public void saveTaskHeadDetail_Failed_firesOnSaveFailed() {
        mRepository.saveTaskHeadDetail(TASKHEAD_DETAIL, mSaveCallback);

        // Failed saving to Local data source
        saveTaskHeadIsSucceed_membersIsFailed(mLocalDataSource);

        verify(mSaveCallback).onSaveFailed();
    }

    @Test
    public void saveTaskHead_toLocal() {
        mRepository.saveTaskHeadDetail(TASKHEAD_DETAIL, mSaveCallback);
        TaskHead taskHead = TASKHEAD_DETAIL.getTaskHead();
        verify(mLocalDataSource).saveTaskHead(eq(taskHead), mSaveCallbackCaptor.capture());
    }

    @Test
    public void saveMembers_toLocal_whenSaveTaskHeadSuccess() {
        mRepository.saveTaskHeadDetail(TASKHEAD_DETAIL, mSaveCallback);

        verify(mLocalDataSource).saveTaskHead(eq(TASKHEAD_DETAIL.getTaskHead()), mSaveCallbackCaptor.capture());
        mSaveCallbackCaptor.getValue().onSaveSuccess();
        verify(mLocalDataSource).saveMembers(eq(TASKHEAD_DETAIL.getMembers()), mSaveCallbackCaptor.capture());
    }

    @Test
    public void saveTaskHeadDetail_toCache_whenSaveTaskHeadAndMembersSucceed() {
        mRepository.saveTaskHeadDetail(TASKHEAD_DETAIL, mSaveCallback);

        saveTaskHeadAndMembersAreSucceed(mLocalDataSource, TASKHEAD_DETAIL);

        TaskHead savedTaskHead = TASKHEAD_DETAIL.getTaskHead();
        // verify that cachedTaskHead and cachedMembers to saved successfully
        // Check cachedTaskHead
        assertThat(mRepository.mCachedTaskHeads.containsKey(savedTaskHead.getId()), is(true));
        // Check members by taskHeadId
        assertThat(mRepository.mCachedMembersOfTaskHead.containsKey(savedTaskHead.getId()), is(true));
        // and check members by member id
        List<Member> members = TASKHEAD_DETAIL.getMembers();
        for(Member member:members) {
            assertThat(mRepository.mCachedMembers.containsKey(member.getId()), is(true));
        }
    }

    /*
    * Delete TaskHeads
    * */
    @Test
    public void deleteTaskHeads_fromLocal() {
        List<String> taskHeadIds = new ArrayList<>();
        taskHeadIds.add(TASKHEADS.get(0).getId());
        taskHeadIds.add(TASKHEADS.get(1).getId());
        mRepository.deleteTaskHeads(taskHeadIds);

        verify(mLocalDataSource).deleteTaskHeads(eq(taskHeadIds));
    }

    @Test
    public void deleteTaskHeads_fromCache() {
        // Save the stubbed taskheadDetails
        List<TaskHeadDetail> taskHeadDetails = new ArrayList<>();
        saveStubbedTaskHeadDetails_toLocal(taskHeadDetails);
        assertThat(mRepository.mCachedTaskHeads.size(), is(2));

        // Delete taskHeads TASKHEADS index 1, 2nd
        List<String> taskheadIds = new ArrayList<>(0);
        taskheadIds.add(taskHeadDetails.get(0).getTaskHead().getId());
        mRepository.deleteTaskHeads(taskheadIds);

        assertThat(mRepository.mCachedTaskHeads.size(), is(1));
        TaskHead notDeletedTaskHead = taskHeadDetails.get(1).getTaskHead();
        assertThat(mRepository.mCachedTaskHeads.containsKey(notDeletedTaskHead.getId()), is(true));
    }

    /*
    * Edit TaskHeadDetail
    * update TaskHead
    * and update Member - add or delete
    * */

    /*
    * convenience methods
    * */
    private void saveStubbedTaskHeadDetails_toLocal(List<TaskHeadDetail> taskHeadDetails) {
        TaskHeadDetail taskHeadDetail0 = new TaskHeadDetail(TASKHEADS.get(0), MEMBERS);
        taskHeadDetails.add(taskHeadDetail0);
        TaskHeadDetail taskHeadDetail1 = new TaskHeadDetail(TASKHEADS.get(1), MEMBERS);
        taskHeadDetails.add(taskHeadDetail1);

        mRepository.saveTaskHeadDetail(taskHeadDetails.get(0), mSaveCallback);
        mRepository.saveTaskHeadDetail(taskHeadDetails.get(1), mSaveCallback);

        saveTaskHeadAndMembersAreSucceed(mLocalDataSource, taskHeadDetail0);
        verify(mLocalDataSource).saveTaskHead(eq(taskHeadDetail1.getTaskHead()), mSaveCallbackCaptor.capture());
        mSaveCallbackCaptor.getValue().onSaveSuccess();
        verify(mLocalDataSource, times(2)).saveMembers(eq(taskHeadDetail1.getMembers()), mSaveCallbackCaptor.capture());
        mSaveCallbackCaptor.getValue().onSaveSuccess();
    }

    private void setTaskHeadsNotAvailable(TaskDataSource dataSource) {
        verify(dataSource).getTaskHeads(mLoadTaskHeadsCallbackCaptor.capture());
        mLoadTaskHeadsCallbackCaptor.getValue().onDataNotAvailable();
    }

    private void setTaskHeadsAvailable(TaskDataSource dataSource, List<TaskHead> taskHeads) {
        verify(dataSource).getTaskHeads(mLoadTaskHeadsCallbackCaptor.capture());
        mLoadTaskHeadsCallbackCaptor.getValue().onTaskHeadsLoaded(taskHeads);
    }

    private void saveTaskHeadAndMembersAreSucceed(TaskDataSource dataSource, TaskHeadDetail taskHeadDetail) {
        // Success to save TaskHead and Members
        verify(dataSource).saveTaskHead(eq(taskHeadDetail.getTaskHead()), mSaveCallbackCaptor.capture());
        mSaveCallbackCaptor.getValue().onSaveSuccess();
        verify(dataSource).saveMembers(eq(taskHeadDetail.getMembers()), mSaveCallbackCaptor.capture());
        mSaveCallbackCaptor.getValue().onSaveSuccess();
    }

    private void saveTaskHeadIsSucceed_membersIsFailed(TaskDataSource dataSource) {
        // Succeed to save taskhead
        verify(dataSource).saveTaskHead(eq(TASKHEAD_DETAIL.getTaskHead()), mSaveCallbackCaptor.capture());
        mSaveCallbackCaptor.getValue().onSaveSuccess();
        // Failed to save members
        verify(dataSource).saveMembers(eq(TASKHEAD_DETAIL.getMembers()), mSaveCallbackCaptor.capture());
        mSaveCallbackCaptor.getValue().onSaveFailed();
    }
}