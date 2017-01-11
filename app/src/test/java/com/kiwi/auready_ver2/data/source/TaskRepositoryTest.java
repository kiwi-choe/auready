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
    @Mock
    private TaskDataSource.EditTaskHeadDetailCallback mEditCallback;
    @Captor
    private ArgumentCaptor<TaskDataSource.EditTaskHeadDetailCallback> mEditCallbackCaptor;
    @Mock
    private TaskDataSource.GetTaskHeadDetailCallback mGetTaskHeadDetailCallback;
    @Captor
    private ArgumentCaptor<TaskDataSource.GetTaskHeadDetailCallback> mGetTaskHeadDetailCallbackCaptor;

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
    public void saveTaskHeadDetail_toLocal() {
        mRepository.saveTaskHeadDetail(TASKHEAD_DETAIL, mSaveCallback);

        verify(mLocalDataSource).saveTaskHeadDetail(eq(TASKHEAD_DETAIL), mSaveCallbackCaptor.capture());
    }

    @Test
    public void whenSaveTaskHeadDetailSucceed_toLocal_firesOnSaveSuccess() {
        mRepository.saveTaskHeadDetail(TASKHEAD_DETAIL, mSaveCallback);

        saveTaskHeadDetailSucceed(mLocalDataSource, TASKHEAD_DETAIL);

        verify(mSaveCallback).onSaveSuccess();
    }

    @Test
    public void saveTaskHeadDetailFailed_toLocal_firesOnSaveFailed() {
        mRepository.saveTaskHeadDetail(TASKHEAD_DETAIL, mSaveCallback);

        // Failed saving to Local data source
        saveTaskHeadDetailFailed(mLocalDataSource);

        verify(mSaveCallback).onSaveFailed();
    }

    @Test
    public void saveTaskHeadDetail_toCache_whenSaveTaskHeadAndMembersSucceed() {
        mRepository.saveTaskHeadDetail(TASKHEAD_DETAIL, mSaveCallback);

        saveTaskHeadDetailSucceed(mLocalDataSource, TASKHEAD_DETAIL);

        TaskHead savedTaskHead = TASKHEAD_DETAIL.getTaskHead();
        // verify that cachedTaskHead and cachedMembers to saved successfully
        // Check cachedTaskHead
        assertThat(mRepository.mCachedTaskHeads.containsKey(savedTaskHead.getId()), is(true));
        // Check members by taskHeadId
        assertThat(mRepository.mCachedMembersOfTaskHead.containsKey(savedTaskHead.getId()), is(true));
        // and check members by member id
        List<Member> members = TASKHEAD_DETAIL.getMembers();
        for (Member member : members) {
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
        List<TaskHeadDetail> taskHeadDetails = saveStubbedTaskHeadDetails_toLocal();
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
    * Update taskHeads order
    * */
    @Test
    public void updateTaskHeadsOrder_fromLocal() {
        // Save the original taskHeads to compare
        List<TaskHeadDetail> savedTaskHeadDetails = saveStubbedTaskHeadDetails_toLocal();
        final TaskHead taskHead0 = savedTaskHeadDetails.get(0).getTaskHead();
        TaskHead taskHead1 = savedTaskHeadDetails.get(1).getTaskHead();

        List<TaskHead> updatingTaskHeads = new ArrayList<>();
        TaskHead updating0 = new TaskHead(taskHead0.getId(), taskHead0.getTitle(), 100);
        updatingTaskHeads.add(updating0);
        TaskHead updating1 = new TaskHead(taskHead1.getId(), taskHead1.getTitle(), 200);
        updatingTaskHeads.add(updating1);
        mRepository.updateTaskHeadOrders(updatingTaskHeads);

        verify(mLocalDataSource).updateTaskHeadOrders(eq(updatingTaskHeads));
    }

    @Test
    public void updateTaskHeadOrders_refreshCache() {
        // Save the original taskHeads to compare
        List<TaskHeadDetail> savedTaskHeadDetails = saveStubbedTaskHeadDetails_toLocal();
        final TaskHead taskHead0 = savedTaskHeadDetails.get(0).getTaskHead();
        TaskHead taskHead1 = savedTaskHeadDetails.get(1).getTaskHead();

        // Updating orders
        List<TaskHead> updatingTaskHeads = new ArrayList<>();
        TaskHead updating0 = new TaskHead(taskHead0.getId(), taskHead0.getTitle(), 100);
        updatingTaskHeads.add(updating0);
        TaskHead updating1 = new TaskHead(taskHead1.getId(), taskHead1.getTitle(), 200);
        updatingTaskHeads.add(updating1);
        mRepository.updateTaskHeadOrders(updatingTaskHeads);

        assertThat(mRepository.mCachedTaskHeads.get(taskHead0.getId()).getOrder(), is(100));
        assertThat(mRepository.mCachedTaskHeads.get(taskHead1.getId()).getOrder(), is(200));
    }

    /*
    * Edit TaskHeadDetail
    * update TaskHead
    * and add or delete members
    * */
    @Test
    public void editTaskHeadDetail_saveEditedTaskHead_addMembers() {
        // Save stubbed taskHeadDetails
        List<TaskHeadDetail> savedTaskHeadDetails = saveStubbedTaskHeadDetails_toLocal();
        TaskHeadDetail original = savedTaskHeadDetails.get(0);
        // Verify that saved cachedMembersOfTaskHead
        assertThat(mRepository.mCachedMembersOfTaskHead.get(original.getTaskHead().getId()).size(), is(3));

        // Edit taskHead
        TaskHead taskHead = original.getTaskHead();
        String newTitle = "newTaskHeadTitle";
        TaskHead editTaskHead = new TaskHead(taskHead.getId(), newTitle, taskHead.getOrder());
        // and Add 2 members
        List<Member> editMembers = new ArrayList<>();
        editMembers.addAll(MEMBERS);
        Member newMember1 = new Member(taskHead.getId(), "friendid", "newMember1");
        editMembers.add(newMember1);
        Member newMember2 = new Member(taskHead.getId(), "friendid", "newMember2");
        editMembers.add(newMember2);

        TaskHeadDetail editTaskHeadDetail = new TaskHeadDetail(editTaskHead, editMembers);
        mRepository.editTaskHeadDetail(editTaskHeadDetail, mEditCallback);

        // Compare members
        // and the result that members are added
        List<Member> addingMembers = getAddingMembers(taskHead.getId(), editMembers);
        verify(mLocalDataSource).editTaskHeadDetail(
                eq(editTaskHead), eq(addingMembers), any(List.class), mEditCallbackCaptor.capture());
        // Verify that getAddingMembers function
        assertThat(addingMembers.size(), is(2));
        mEditCallbackCaptor.getValue().onEditSuccess();
        assertThat(mRepository.mCachedMembers.containsKey(newMember1.getId()), is(true));
        assertThat(mRepository.mCachedMembers.containsKey(newMember2.getId()), is(true));
    }

    @Test
    public void editTaskHeadDetail_saveEditedTaskHead_deleteMembers() {
        // Save stubbed taskHeadDetails
        List<TaskHeadDetail> savedTaskHeadDetails = saveStubbedTaskHeadDetails_toLocal();
        TaskHeadDetail original = savedTaskHeadDetails.get(0);

        TaskHead taskHead = original.getTaskHead();
        // Delete 1 members
        List<Member> editMembers = new ArrayList<>();
        editMembers.addAll(MEMBERS);
        Member deletingMember = MEMBERS.get(0);
        editMembers.remove(deletingMember);

        TaskHeadDetail editTaskHeadDetail = new TaskHeadDetail(taskHead, editMembers);
        mRepository.editTaskHeadDetail(editTaskHeadDetail, mEditCallback);

        // Compare members
        // and the result that members are deleted
        List<String> deletingMemberIds = getDeletingMemberIds(taskHead.getId(), editMembers);
        // Verify that getAddingMembers function
        assertThat(deletingMemberIds.size(), is(1));

        verify(mLocalDataSource).editTaskHeadDetail(
                eq(taskHead), any(List.class), eq(deletingMemberIds), mEditCallbackCaptor.capture());
        mEditCallbackCaptor.getValue().onEditSuccess();
        assertThat(mRepository.mCachedMembers.containsKey(deletingMember.getId()), is(false));
    }

    @Test
    public void whenEditTaskHeadDetailSucceed_toLocal_firesOnEditSuccess() {
        mRepository.editTaskHeadDetail(TASKHEAD_DETAIL, mEditCallback);

        verify(mLocalDataSource).editTaskHeadDetail(
                eq(TASKHEAD_DETAIL.getTaskHead()), any(List.class), any(List.class), mEditCallbackCaptor.capture());
        mEditCallbackCaptor.getValue().onEditSuccess();

        verify(mEditCallback).onEditSuccess();
    }

    /*
    * Get a TaskHeadDetail
    * */
    @Test
    public void getTaskHeadDetail_fromLocal() {
        String taskHeadId = TASKHEAD_DETAIL.getTaskHead().getId();
        mRepository.getTaskHeadDetail(taskHeadId, mGetTaskHeadDetailCallback);

        verify(mLocalDataSource).getTaskHeadDetail(eq(taskHeadId),
                any(TaskDataSource.GetTaskHeadDetailCallback.class));
    }

    @Test
    public void getTaskHeadDetailWithBothDataSourceUnavailable_firesOnDataUnavailable() {
        String taskHeadId = TASKHEAD_DETAIL.getTaskHead().getId();
        mRepository.getTaskHeadDetail(taskHeadId, mGetTaskHeadDetailCallback);

        setTaskHeadDetailNotAvailable(mLocalDataSource, taskHeadId);

        verify(mGetTaskHeadDetailCallback).onDataNotAvailable();
    }

    @Test
    public void getTaskHeadDetail_checkCache() {
        saveStubbedTaskHeadDetails_toLocal();

        String taskHeadId = TASKHEADS.get(0).getId();
        mRepository.getTaskHeadDetail(taskHeadId, mGetTaskHeadDetailCallback);

        assertThat(mRepository.mCachedMembersOfTaskHead.get(taskHeadId).get(0).getName(), is(MEMBERS.get(0).getName()));
        assertThat(mRepository.mCachedMembersOfTaskHead.get(taskHeadId).get(1).getName(), is(MEMBERS.get(1).getName()));
        assertThat(mRepository.mCachedMembersOfTaskHead.get(taskHeadId).get(2).getName(), is(MEMBERS.get(2).getName()));
    }

    /*
    * convenience methods
    * */
    private void setTaskHeadDetailNotAvailable(TaskDataSource dataSource, String taskHeadId) {
        verify(dataSource).getTaskHeadDetail(eq(taskHeadId), mGetTaskHeadDetailCallbackCaptor.capture());
        mGetTaskHeadDetailCallbackCaptor.getValue().onDataNotAvailable();
    }

    private List<String> getDeletingMemberIds(String taskHeadId, List<Member> editMembers) {
        // Compare cachedMembersOfTaskHead to editMembers
        List<String> deletingMemberIds = new ArrayList<>();

        if (mRepository.mCachedMembersOfTaskHead != null) {
            List<Member> cachedMembers = mRepository.mCachedMembersOfTaskHead.get(taskHeadId);
            for (Member member : cachedMembers) {
                if (!editMembers.contains(member)) {
                    deletingMemberIds.add(member.getId());
                }
            }
        }
        return deletingMemberIds;
    }

    private List<Member> getAddingMembers(String taskHeadId, List<Member> editMembers) {
        // Compare cachedMembersOfTaskHead to editMembers
        List<Member> addingMembers = new ArrayList<>();

        if (mRepository.mCachedMembersOfTaskHead != null) {
            List<Member> cachedMembers = mRepository.mCachedMembersOfTaskHead.get(taskHeadId);
            for (Member member : editMembers) {
                if (!cachedMembers.contains(member)) {
                    addingMembers.add(member);
                }
            }
        }
        return addingMembers;
    }

    private List<TaskHeadDetail> saveStubbedTaskHeadDetails_toLocal() {
        List<TaskHeadDetail> taskHeadDetails = new ArrayList<>();
        TaskHeadDetail taskHeadDetail0 = new TaskHeadDetail(TASKHEADS.get(0), MEMBERS);
        taskHeadDetails.add(taskHeadDetail0);
        TaskHeadDetail taskHeadDetail1 = new TaskHeadDetail(TASKHEADS.get(1), MEMBERS);
        taskHeadDetails.add(taskHeadDetail1);

        mRepository.saveTaskHeadDetail(taskHeadDetails.get(0), mSaveCallback);
        mRepository.saveTaskHeadDetail(taskHeadDetails.get(1), mSaveCallback);

        saveTaskHeadDetailSucceed(mLocalDataSource, taskHeadDetail0);
        saveTaskHeadDetailSucceed(mLocalDataSource, taskHeadDetail1);

        return taskHeadDetails;
    }

    private void setTaskHeadsNotAvailable(TaskDataSource dataSource) {
        verify(dataSource).getTaskHeads(mLoadTaskHeadsCallbackCaptor.capture());
        mLoadTaskHeadsCallbackCaptor.getValue().onDataNotAvailable();
    }

    private void setTaskHeadsAvailable(TaskDataSource dataSource, List<TaskHead> taskHeads) {
        verify(dataSource).getTaskHeads(mLoadTaskHeadsCallbackCaptor.capture());
        mLoadTaskHeadsCallbackCaptor.getValue().onTaskHeadsLoaded(taskHeads);
    }

    private void saveTaskHeadDetailSucceed(TaskDataSource dataSource, TaskHeadDetail taskHeadDetail) {
        // Success to save TaskHead and Members
        verify(dataSource).saveTaskHeadDetail(eq(taskHeadDetail), mSaveCallbackCaptor.capture());
        mSaveCallbackCaptor.getValue().onSaveSuccess();
    }

    private void saveTaskHeadDetailFailed(TaskDataSource dataSource) {
        verify(dataSource).saveTaskHeadDetail(eq(TASKHEAD_DETAIL), mSaveCallbackCaptor.capture());
        mSaveCallbackCaptor.getValue().onSaveFailed();
    }
}