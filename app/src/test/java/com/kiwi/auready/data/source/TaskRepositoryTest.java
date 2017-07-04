package com.kiwi.auready.data.source;

import com.google.common.collect.Lists;
import com.kiwi.auready.data.Member;
import com.kiwi.auready.data.Task;
import com.kiwi.auready.data.TaskHead;
import com.kiwi.auready.data.TaskHeadDetail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.kiwi.auready.StubbedData.TaskStub.MEMBERS;
import static com.kiwi.auready.StubbedData.TaskStub.MEMBERS1;
import static com.kiwi.auready.StubbedData.TaskStub.TASK;
import static com.kiwi.auready.StubbedData.TaskStub.TASKHEAD;
import static com.kiwi.auready.StubbedData.TaskStub.TASKHEADS;
import static com.kiwi.auready.StubbedData.TaskStub.TASKHEAD_DETAIL;
import static com.kiwi.auready.StubbedData.TaskStub.TASKHEAD_DETAILS;
import static com.kiwi.auready.StubbedData.TaskStub.TASKS;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of the in-memory repository with cache.
 */
public class TaskRepositoryTest {

    private TaskRepository mRepository;

    @Mock
    private TaskDataSource mLocalDataSource;
    @Mock
    private TaskDataSource mRemoteDataSource;

    @Mock
    private TaskDataSource.LoadTaskHeadDetailsCallback mLoadTaskHeadDetailsCallback;
    @Captor
    private ArgumentCaptor<TaskDataSource.LoadTaskHeadDetailsCallback> mLoadTaskHeadDetailsCallbackCaptor;

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

    @Mock
    private TaskDataSource.LoadTasksCallback mLoadTasksCallback;
    @Captor
    private ArgumentCaptor<TaskDataSource.LoadTasksCallback> mLoadTasksCallbackCaptor;
    @Mock
    private TaskDataSource.LoadMembersCallback mLoadMembersCallback;
    @Captor
    private ArgumentCaptor<TaskDataSource.LoadMembersCallback> mLoadMembersCallbackCaptor;

    @Mock
    private TaskDataSource.DeleteTaskHeadsCallback mDeleteTaskHeadsCallback;
    @Captor
    private ArgumentCaptor<TaskDataSource.DeleteTaskHeadsCallback> mDeleteTaskHeadsCallbackCaptor;
    @Mock
    private TaskDataSource.SaveTaskCallback mSaveTaskCallback;
    @Captor
    private ArgumentCaptor<TaskDataSource.SaveTaskCallback> mSaveTaskCallbackCaptor;
    @Mock
    private TaskDataSource.DeleteTaskCallback mDeleteTaskCallback;
    @Captor
    private ArgumentCaptor<TaskDataSource.DeleteTaskCallback> mDeleteTaskCallbackCaptor;
    @Mock
    private TaskDataSource.UpdateTaskHeadOrdersCallback mUpdateTaskHeadOrdersCallback;
    @Captor
    private ArgumentCaptor<TaskDataSource.UpdateTaskHeadOrdersCallback> mUpdateTaskHeadOrdersCallbackCaptor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mRepository = TaskRepository.getInstance(mRemoteDataSource, mLocalDataSource);
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
        mRepository.getTaskHeadDetails(mLoadTaskHeadDetailsCallback);

        // Local data source has no data available
        setTaskHeadDetailsNotAvailable(mLocalDataSource);
        // and Remote data source has no data available too
        setTaskHeadDetailsNotAvailable(mRemoteDataSource);

        verify(mLoadTaskHeadDetailsCallback).onDataNotAvailable();
    }

    @Test
    public void getTaskHeads_requestsTaskHeadsFromLocal() {
        mRepository.getTaskHeadDetails(mLoadTaskHeadDetailsCallback);

        setTaskHeadDetailsAvailable(mLocalDataSource, TASKHEAD_DETAILS);

        // Then taskHeads are loaded from the local
        verify(mLocalDataSource).getTaskHeadDetails(any(TaskDataSource.LoadTaskHeadDetailsCallback.class));
        verify(mLoadTaskHeadDetailsCallback).onTaskHeadDetailsLoaded(TASKHEAD_DETAILS);
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

//    @Test
//    public void saveTaskHeadDetail_savesToServiceAPI() {
//        mRepository.saveTaskHeadDetail(TASKHEAD_DETAIL, mSaveCallback);
//
//        // Then the service API and persistent repository are called
//        saveTaskHeadDetailSucceed(mRemoteDataSource, TASKHEAD_DETAIL);
//        verify(mSaveCallback).onSaveSuccess();
//    }

    /*
    * Delete TaskHeads
    * */
    @Test
    public void deleteTaskHeads_fromLocal() {
        List<String> taskHeadIds = new ArrayList<>();
        taskHeadIds.add(TASKHEADS.get(0).getId());
        taskHeadIds.add(TASKHEADS.get(1).getId());
        mRepository.deleteTaskHeads(taskHeadIds, mDeleteTaskHeadsCallback);

        verify(mLocalDataSource).deleteTaskHeads(eq(taskHeadIds), mDeleteTaskHeadsCallbackCaptor.capture());
    }

    @Test
    public void deleteTaskHeads_fromRemote_whenSucceedInLocal() {
        List<String> taskHeadIds = new ArrayList<>();
        taskHeadIds.add(TASKHEADS.get(0).getId());
        taskHeadIds.add(TASKHEADS.get(1).getId());
        mRepository.deleteTaskHeads(taskHeadIds, mDeleteTaskHeadsCallback);

        verify(mLocalDataSource).deleteTaskHeads(eq(taskHeadIds), mDeleteTaskHeadsCallbackCaptor.capture());
        mDeleteTaskHeadsCallbackCaptor.getValue().onDeleteSuccess();

        verify(mRemoteDataSource).deleteTaskHeads(eq(taskHeadIds), mDeleteTaskHeadsCallbackCaptor.capture());
        mDeleteTaskHeadsCallbackCaptor.getValue().onDeleteSuccess();
    }

    @Test
    public void deleteTaskHeads_fromCache() {
        // Save the stubbed taskheadDetails
        List<TaskHeadDetail> taskHeadDetails = saveStubbedTaskHeadDetails_toLocal();
        assertThat(mRepository.mCachedTaskHeads.size(), is(2));

        // Delete taskHeads TASKHEADS index 1, 2nd
        List<String> taskheadIds = new ArrayList<>(0);
        taskheadIds.add(taskHeadDetails.get(0).getTaskHead().getId());
        mRepository.deleteTaskHeads(taskheadIds, mDeleteTaskHeadsCallback);

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
        TaskHead updating0 = new TaskHead(taskHead0.getId(), taskHead0.getTitle(), 100, taskHead0.getColor());
        updatingTaskHeads.add(updating0);
        TaskHead updating1 = new TaskHead(taskHead1.getId(), taskHead1.getTitle(), 200, taskHead1.getColor());
        updatingTaskHeads.add(updating1);
        mRepository.updateTaskHeadOrders(updatingTaskHeads, mUpdateTaskHeadOrdersCallback);

        verify(mLocalDataSource).updateTaskHeadOrders(eq(updatingTaskHeads), mUpdateTaskHeadOrdersCallbackCaptor.capture());
    }

    @Test
    public void updateTaskHeadOrders_refreshCache() {
        // Save the original taskHeads to compare
        List<TaskHeadDetail> savedTaskHeadDetails = saveStubbedTaskHeadDetails_toLocal();
        final TaskHead taskHead0 = savedTaskHeadDetails.get(0).getTaskHead();
        TaskHead taskHead1 = savedTaskHeadDetails.get(1).getTaskHead();

        // Updating orders
        List<TaskHead> updatingTaskHeads = new ArrayList<>();
        TaskHead updating0 = new TaskHead(taskHead0.getId(), taskHead0.getTitle(), 100, taskHead0.getColor());
        updatingTaskHeads.add(updating0);
        TaskHead updating1 = new TaskHead(taskHead1.getId(), taskHead1.getTitle(), 200, taskHead1.getColor());
        updatingTaskHeads.add(updating1);
        mRepository.updateTaskHeadOrders(updatingTaskHeads, mUpdateTaskHeadOrdersCallback);

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
        TaskHead editTaskHead = new TaskHead(taskHead.getId(), newTitle, taskHead.getOrder(), taskHead.getColor());
        // and Add 2 members
        List<Member> editMembers = new ArrayList<>();
        editMembers.addAll(MEMBERS);
        Member newMember1 = new Member(taskHead.getId(), "friendid", "newMember1", "newMember1_email");
        editMembers.add(newMember1);
        Member newMember2 = new Member(taskHead.getId(), "friendid", "newMember2", "newMember2_email");
        editMembers.add(newMember2);

        TaskHeadDetail editTaskHeadDetail = new TaskHeadDetail(editTaskHead, editMembers);
        mRepository.editTaskHeadDetailInRepo(editTaskHeadDetail, mEditCallback);

        // Compare members
        // and the result that members are added
        List<Member> addingMembers = getAddingMembers(taskHead.getId(), editMembers);
        verify(mLocalDataSource).editTaskHeadDetail(
                eq(editTaskHead), eq(addingMembers), mEditCallbackCaptor.capture());
        // Verify that getAddingMembers function
        assertThat(addingMembers.size(), is(2));
        mEditCallbackCaptor.getValue().onEditSuccess();
        assertThat(mRepository.mCachedMembers.containsKey(newMember1.getId()), is(true));
        assertThat(mRepository.mCachedMembers.containsKey(newMember2.getId()), is(true));
    }

    @Test
    public void whenEditTaskHeadDetailSucceed_toLocal_firesOnEditSuccess() {
        mRepository.editTaskHeadDetailInRepo(TASKHEAD_DETAIL, mEditCallback);

        verify(mLocalDataSource).editTaskHeadDetail(
                eq(TASKHEAD_DETAIL.getTaskHead()), any(List.class), mEditCallbackCaptor.capture());
        mEditCallbackCaptor.getValue().onEditSuccess();

        verify(mEditCallback).onEditSuccess();
    }

    @Test
    public void editTaskHeadDetailToRemote_whenLocalIsSucceed() {
        List<Member> addingMembers = Lists.newArrayList(MEMBERS1.get(0), MEMBERS1.get(1));
        mRepository.editTaskHeadDetail(TASKHEAD_DETAIL.getTaskHead(), addingMembers, mEditCallback);

        verify(mLocalDataSource).editTaskHeadDetail(
                eq(TASKHEAD_DETAIL.getTaskHead()), any(List.class), mEditCallbackCaptor.capture());
        mEditCallbackCaptor.getValue().onEditSuccess();

        verify(mRemoteDataSource).editTaskHeadDetail(
                eq(TASKHEAD_DETAIL.getTaskHead()), anyListOf(Member.class), mEditCallbackCaptor.capture());
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
    public void getTaskHeadDetail_fromRemote() {
        // Set forceToUpdate is true
        mRepository.forceUpdateLocalATaskHeadDetail();
        String taskHeadId = TASKHEAD_DETAIL.getTaskHead().getId();
        mRepository.getTaskHeadDetail(taskHeadId, mGetTaskHeadDetailCallback);

        verify(mRemoteDataSource).getTaskHeadDetail(eq(taskHeadId), mGetTaskHeadDetailCallbackCaptor.capture());
        verify(mGetTaskHeadDetailCallback).onTaskHeadDetailLoaded(TASKHEAD_DETAIL);
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
    * Get Tasks
    * */
    @Test
    public void getTasks_fromLocal() {
        String memberId = "stubbedMemberId";
        mRepository.getTasksOfMember(memberId, mLoadTasksCallback);

        verify(mLocalDataSource).getTasksOfMember(eq(memberId), mLoadTasksCallbackCaptor.capture());
        mLoadTasksCallbackCaptor.getValue().onTasksLoaded(TASKS);

        assertThat(mRepository.mCachedTasks.size(), is(TASKS.size()));
    }

    @Test
    public void getTasksWithLocalUnavailable_firesOnDataNotAvailable() {
        String memberId = "stubbedMemberId";
        mRepository.getTasksOfMember(memberId, mLoadTasksCallback);

        // Local data source has no data available
        verify(mLocalDataSource).getTasksOfMember(eq(memberId), mLoadTasksCallbackCaptor.capture());
        mLoadTasksCallbackCaptor.getValue().onDataNotAvailable();

        verify(mLoadTasksCallback).onDataNotAvailable();
    }

    @Test
    public void getTasksWithLocal_firesOnTaskLoaded() {
        String memberId = "stubbedMemberId";
        mRepository.getTasksOfMember(memberId, mLoadTasksCallback);

        // Local data source has no data available
        verify(mLocalDataSource).getTasksOfMember(eq(memberId), mLoadTasksCallbackCaptor.capture());
        mLoadTasksCallbackCaptor.getValue().onTasksLoaded(TASKS);

        verify(mLoadTasksCallback).onTasksLoaded(anyListOf(Task.class));
    }

    /*
    * Save Task
    * */
    @Test
    public void saveTask_toLocal() {
        mRepository.saveTask(TASK, new ArrayList<Task>(), mSaveTaskCallback);

        verify(mLocalDataSource).saveTask(eq(TASK), anyListOf(Task.class), mSaveTaskCallbackCaptor.capture());
        mSaveTaskCallbackCaptor.getValue().onSaveSuccess(new ArrayList<Task>());
        verify(mSaveTaskCallback).onSaveSuccess(new ArrayList<Task>());

        assertThat(mRepository.mCachedTasks.containsKey(TASK.getId()), is(true));
    }

    /*
    * Delete a Task
    * */
    @Test
    public void deleteTask_fromLocal() {
        String taskId = TASKS.get(0).getId();
        String memberId = TASKS.get(0).getMemberId();
        mRepository.deleteTask(memberId, taskId, new ArrayList<Task>(), mDeleteTaskCallback);

        verify(mLocalDataSource).deleteTask(eq(memberId), eq(taskId), anyListOf(Task.class), mDeleteTaskCallback);
    }

    @Test
    public void deleteTask_fromCache() {
        // Save the stubbed taskheadDetails
        mRepository.saveTask(TASKS.get(0), new ArrayList<Task>(), mSaveTaskCallback);
        mRepository.saveTask(TASKS.get(1), new ArrayList<Task>(), mSaveTaskCallback);
        mRepository.saveTask(TASKS.get(2), new ArrayList<Task>(), mSaveTaskCallback);
        assertThat(mRepository.mCachedTasks.size(), is(3));

        // Delete taskHeads TASKHEADS index 1, 2nd
        mRepository.deleteTask(TASKS.get(0).getMemberId(), TASKS.get(1).getId(), new ArrayList<Task>(), mDeleteTaskCallback);

        assertThat(mRepository.mCachedTasks.size(), is(2));
        assertThat(mRepository.mCachedTasks.containsKey(TASKS.get(0).getId()), is(true));
        assertThat(mRepository.mCachedTasks.containsKey(TASKS.get(1).getId()), is(false));
        assertThat(mRepository.mCachedTasks.containsKey(TASKS.get(2).getId()), is(true));
    }

    /*
    * Edit Tasks
    * */
    @Test
    public void editTasks_toLocal() {
        String memberId0 = "stubbedMemberId0";
        String memberId1 = "stubbedMemberId1";
        List<Task> TASKS0 = Lists.newArrayList(
                new Task("stubbedTask0", memberId0, "description", 0),
                new Task("stubbedTask1", memberId0, "description2", true, 0));
        List<Task> TASKS1 = Lists.newArrayList(
                new Task("stubbedTask2", memberId1, "description3", true, 0));

        // Save tasks
        Map<String, List<Task>> cachedTasks = new LinkedHashMap<>();
        cachedTasks.put(memberId0, TASKS0);
        cachedTasks.put(memberId1, TASKS1);
        saveStubbedTasks(TASKS0);
        saveStubbedTasks(TASKS1);

        // Update tasks
        TASKS0.get(0).setDescription("editDescription!!!");
        cachedTasks.put(memberId0, TASKS0);
        TASKS1.get(0).setDescription("edit description3");
        cachedTasks.put(memberId1, TASKS1);

        mRepository.editTasks("stubbedTaskHeadId", cachedTasks);

        verify(mLocalDataSource).editTasks(eq("stubbedTaskHeadId"), eq(cachedTasks));

        assertThat(mRepository.mCachedTasks.get(TASKS0.get(0).getId()).getDescription(), is("editDescription!!!"));
        assertThat(mRepository.mCachedTasks.get(TASKS1.get(0).getId()).getDescription(), is("edit description3"));
    }

    private void saveStubbedTasks(List<Task> tasks) {
        for (Task task : tasks) {
            mRepository.saveTask(task, new ArrayList<Task>(), mSaveTaskCallback);
        }
    }

    /*
    * Get members
    * */
    @Test
    public void getMembers_fromLocal_whenLoadFromRemoteIsFailed() {
        String taskHeadId = TASKHEAD.getId();
        mRepository.getMembers(taskHeadId, mLoadMembersCallback);

        setGettingMembersNotAvailable(mRemoteDataSource, taskHeadId);

        verify(mLocalDataSource).getMembers(eq(taskHeadId), mLoadMembersCallbackCaptor.capture());
        mLoadMembersCallbackCaptor.getValue().onMembersLoaded(MEMBERS);

        assertThat(mRepository.mCachedMembersOfTaskHead.containsKey(taskHeadId), is(true));
        assertThat(mRepository.mCachedMembers.size(), is(MEMBERS.size()));
    }

    @Test
    public void getMembers_fromRemote_andUpdateCaches() {
        String taskHeadId = TASKHEAD.getId();
        mRepository.getMembers(taskHeadId, mLoadMembersCallback);

        verify(mRemoteDataSource).getMembers(eq(taskHeadId), mLoadMembersCallbackCaptor.capture());
        mLoadMembersCallbackCaptor.getValue().onMembersLoaded(MEMBERS);

        verify(mLoadMembersCallback).onMembersLoaded(eq(MEMBERS));

        assertThat(mRepository.mCachedMembersOfTaskHead.containsKey(taskHeadId), is(true));
        assertThat(mRepository.mCachedMembers.size(), is(MEMBERS.size()));
    }

    @Test
    public void getTaskHeadsCount() {
        List<TaskHeadDetail> taskHeadDetails = saveStubbedTaskHeadDetails_toLocal();
        int count = mRepository.getTaskHeadsCount();
        assertThat(count, is(taskHeadDetails.size()));
        assertThat(taskHeadDetails.size(), is(mRepository.mCachedTaskHeads.size()));
    }

    /*
    * convenience methods
    * */
    private void setGettingMembersNotAvailable(TaskDataSource dataSource, String taskHeadId) {
        verify(dataSource).getMembers(eq(taskHeadId), mLoadMembersCallbackCaptor.capture());
        mLoadMembersCallbackCaptor.getValue().onDataNotAvailable();
    }

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

    private void setTaskHeadDetailsNotAvailable(TaskDataSource dataSource) {
        verify(dataSource).getTaskHeadDetails(mLoadTaskHeadDetailsCallbackCaptor.capture());
        mLoadTaskHeadDetailsCallbackCaptor.getValue().onDataNotAvailable();
    }

    private void setTaskHeadDetailsAvailable(TaskDataSource dataSource, List<TaskHeadDetail> taskHeadDetails) {
        verify(dataSource).getTaskHeadDetails(mLoadTaskHeadDetailsCallbackCaptor.capture());
        mLoadTaskHeadDetailsCallbackCaptor.getValue().onTaskHeadDetailsLoaded(taskHeadDetails);
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