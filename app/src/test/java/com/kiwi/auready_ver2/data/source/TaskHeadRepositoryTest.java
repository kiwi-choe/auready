package com.kiwi.auready_ver2.data.source;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.TaskHead;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of the in-memory repository with cache.
 */
public class TaskHeadRepositoryTest {

    // member is one(me)
    private static List<Friend> MEMBERS = Lists.newArrayList(new Friend("email1", "name1"), new Friend("email2", "name2"),
            new Friend("email3", "name3"));
    private static final List<TaskHead> TASKHEADS =
            Lists.newArrayList(new TaskHead("title1", MEMBERS, 0),
                    new TaskHead("title2", MEMBERS, 1), new TaskHead("title3", MEMBERS, 2));
    private static final String TASKHEAD_ID = "123";

    private TaskHeadRepository mTaskHeadsRepository;

    @Mock
    private TaskHeadDataSource mTaskHeadRemoteDataSource;
    @Mock
    private TaskHeadDataSource mTaskHeadLocalDataSource;
    @Mock
    private TaskHeadDataSource.GetTaskHeadCallback mGetTaskCallback;
    @Mock
    private TaskHeadDataSource.LoadTaskHeadsCallback mLoadTaskHeadsCallback;

    @Captor
    private ArgumentCaptor<TaskHeadDataSource.LoadTaskHeadsCallback> mTaskHeadsCallbackCaptor;
    @Captor
    private ArgumentCaptor<TaskHeadDataSource.GetTaskHeadCallback> mTaskHeadCallbackCaptor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mTaskHeadsRepository = TaskHeadRepository.getInstance(
                mTaskHeadRemoteDataSource, mTaskHeadLocalDataSource);
    }

    @After
    public void destroyRepositoryInstance() {
        TaskHeadRepository.destroyInstance();
    }

    /*
    * Get taskHeads
    * */
    @Test
    public void getTaskHeadsWithLocalUnavailable_taskHeadsAreRetrievedFromRemote() {
        mTaskHeadsRepository.getTaskHeads(mLoadTaskHeadsCallback);
        // Local data source has no data available
        setTaskHeadsNotAvailable(mTaskHeadLocalDataSource);
        // And Remote source has data available
        setTaskHeadsAvailable(mTaskHeadRemoteDataSource, TASKHEADS);

        verify(mLoadTaskHeadsCallback).onTaskHeadsLoaded(TASKHEADS);
    }

    @Test
    public void getTaskHeads_requestsTaskHeadsFromLocal() {
        // When taskHeads are requested from the taskHeads repository
        mTaskHeadsRepository.getTaskHeads(mLoadTaskHeadsCallback);

        // Then taskHeads are loaded from the local
        verify(mTaskHeadLocalDataSource).getTaskHeads(any(TaskHeadDataSource.LoadTaskHeadsCallback.class));
    }

    @Test
    public void getTaskHeadsWithBothDataSourcesUnavailable_firesOnDataUnavailable() {
        mTaskHeadsRepository.getTaskHeads(mLoadTaskHeadsCallback);

        setTaskHeadsNotAvailable(mTaskHeadLocalDataSource);
        setTaskHeadsNotAvailable(mTaskHeadRemoteDataSource);

        verify(mLoadTaskHeadsCallback).onDataNotAvailable();
    }

    /*
    * Get a TaskHead
    * */
    @Test
    public void getTaskHead_fromLocal() {
        mTaskHeadsRepository.getTaskHead(TASKHEAD_ID, mGetTaskCallback);

        verify(mTaskHeadLocalDataSource).getTaskHead(eq(TASKHEAD_ID), any(
                TaskHeadDataSource.GetTaskHeadCallback.class));
    }

    @Test
    public void getTaskHeadWithBothDataSourceUnavailable_firesOnDataUnavailable() {
        mTaskHeadsRepository.getTaskHead(TASKHEAD_ID, mGetTaskCallback);

        setTaskHeadNotAvailable(mTaskHeadLocalDataSource, TASKHEAD_ID);
        setTaskHeadNotAvailable(mTaskHeadRemoteDataSource, TASKHEAD_ID);

        verify(mGetTaskCallback).onDataNotAvailable();
    }

    /*
    * Save and update a TaskHead
    * */
    @Test
    public void saveTaskHead_retrieveTaskHead() {
        final TaskHead taskHead = new TaskHead();
        mTaskHeadsRepository.saveTaskHead(taskHead);

        verify(mTaskHeadLocalDataSource).saveTaskHead(taskHead);

        assertThat(mTaskHeadsRepository.mCachedTaskHeads.containsKey(taskHead.getId()), is(true));

        mTaskHeadsRepository.getTaskHeads(new TaskHeadDataSource.LoadTaskHeadsCallback() {
            @Override
            public void onTaskHeadsLoaded(List<TaskHead> taskHeads) {
                assertThat(taskHeads.get(0).getId(), is(taskHead.getId()));
            }

            @Override
            public void onDataNotAvailable() {
                fail();
            }
        });
    }

    @Test
    public void editTaskHead_fromLocal_andUpdateCache() {
        // Save a new taskHead
        String TITLE = "title_test";
        TaskHead taskHead = new TaskHead(TITLE, MEMBERS, 0);
        mTaskHeadsRepository.saveTaskHead(taskHead);

        // Modify the value of title and members
        String modifiedTitle = "editTitle";
        final List<Friend> modifiedMembers = MEMBERS;
        modifiedMembers.add(new Friend("new member email", "new member name"));
        // Update the changed members
        mTaskHeadsRepository.editTaskHead(taskHead.getId(), modifiedTitle, modifiedMembers);
        verify(mTaskHeadLocalDataSource).editTaskHead(eq(taskHead.getId()), eq(modifiedTitle), eq(modifiedMembers));

        // Update cache
        assertThat(mTaskHeadsRepository.mCachedTaskHeads.get(taskHead.getId()).getTitle(), is(modifiedTitle));
    }

    @Test
    public void addMembers_toLocal_andUpdateCache() {
        // Save a new taskHead
        TaskHead taskHead = new TaskHead("title", MEMBERS, 0);
        mTaskHeadsRepository.saveTaskHead(taskHead);

        // Add members
        final List<Friend> addedMembers = MEMBERS;
        addedMembers.add(new Friend("new member email", "new member name"));
        // Update the added members to repo
        mTaskHeadsRepository.addMembers(taskHead.getId(), addedMembers);
        verify(mTaskHeadLocalDataSource).addMembers(eq(taskHead.getId()), eq(addedMembers));

        // update cache
        assertThat(mTaskHeadsRepository.mCachedTaskHeads.get(taskHead.getId()).getMembers().size(), is(addedMembers.size()));
    }

    @Test
    public void deleteTaskHeads_fromCache() {
        // Save the stubbed taskheads
        mTaskHeadsRepository.saveTaskHead(TASKHEADS.get(0));
        mTaskHeadsRepository.saveTaskHead(TASKHEADS.get(1));
        mTaskHeadsRepository.saveTaskHead(TASKHEADS.get(2));

        // Delete taskHeads TASKHEADS index 1, 2nd
        List<String> taskheadIds = new ArrayList<>(0);
        taskheadIds.add(TASKHEADS.get(1).getId());
        taskheadIds.add(TASKHEADS.get(2).getId());
        mTaskHeadsRepository.deleteTaskHeads(taskheadIds);

        assertThat(mTaskHeadsRepository.mCachedTaskHeads.size(), is(1));
        assertThat(mTaskHeadsRepository.mCachedTaskHeads.containsKey(TASKHEADS.get(0).getId()), is(true));
    }

    @Test
    public void deleteTaskHeads_fromLocal_andRetrieveTaskHeads() {
        // Save the stubbed taskheads
        mTaskHeadsRepository.saveTaskHead(TASKHEADS.get(0));
        mTaskHeadsRepository.saveTaskHead(TASKHEADS.get(1));
        mTaskHeadsRepository.saveTaskHead(TASKHEADS.get(2));

        // Delete taskHeads index 0, 1st
        List<String> taskheadIds = new ArrayList<>(0);
        taskheadIds.add(TASKHEADS.get(0).getId());
        taskheadIds.add(TASKHEADS.get(1).getId());
        mTaskHeadsRepository.deleteTaskHeads(taskheadIds);
        verify(mTaskHeadLocalDataSource).deleteTaskHeads(taskheadIds);

        // Verify that there is only TASKHEADS.get(2)
        mTaskHeadsRepository.getTaskHeads(new TaskHeadDataSource.LoadTaskHeadsCallback() {
            @Override
            public void onTaskHeadsLoaded(List<TaskHead> taskHeads) {
                assertThat(taskHeads.get(0).getTitle(), is(TASKHEADS.get(2).getTitle()));
            }

            @Override
            public void onDataNotAvailable() {
                fail();
            }
        });
    }

    @Test
    public void deleteAllTaskHeads_deleteTaskHeadsFromLocal() {
        // Save 3 stub taskheads in the repository
        List<Friend> memebers = MEMBERS = Lists.newArrayList(new Friend("email1", "name1"), new Friend("email2", "name2"),
                new Friend("email3", "name3"));
        TaskHead newTaskHead = new TaskHead("title1", memebers, 0);
        mTaskHeadsRepository.saveTaskHead(newTaskHead);
        TaskHead newTaskHead2 = new TaskHead("title2", memebers, 1);
        mTaskHeadsRepository.saveTaskHead(newTaskHead2);
        TaskHead newTaskHead3 = new TaskHead("title3", memebers, 2);
        mTaskHeadsRepository.saveTaskHead(newTaskHead3);

        mTaskHeadsRepository.deleteAllTaskHeads();

        verify(mTaskHeadLocalDataSource).deleteAllTaskHeads();

        assertThat(mTaskHeadsRepository.mCachedTaskHeads.size(), is(0));
    }

    /*
    * convenience methods
    * */
    private void setTaskHeadsNotAvailable(TaskHeadDataSource dataSource) {
        verify(dataSource).getTaskHeads(mTaskHeadsCallbackCaptor.capture());
        mTaskHeadsCallbackCaptor.getValue().onDataNotAvailable();
    }

    private void setTaskHeadsAvailable(TaskHeadDataSource dataSource, List<TaskHead> taskHeads) {
        verify(dataSource).getTaskHeads(mTaskHeadsCallbackCaptor.capture());
        mTaskHeadsCallbackCaptor.getValue().onTaskHeadsLoaded(taskHeads);
    }

    private void setTaskHeadNotAvailable(TaskHeadDataSource dataSource, String taskheadId) {
        verify(dataSource).getTaskHead(eq(taskheadId), mTaskHeadCallbackCaptor.capture());
        mTaskHeadCallbackCaptor.getValue().onDataNotAvailable();
    }
}