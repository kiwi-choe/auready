package com.kiwi.auready_ver2.data.source;

import com.kiwi.auready_ver2.data.TaskHead;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static com.kiwi.auready_ver2.StubbedData.TaskStub.TASKHEADS;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of the in-memory repository with cache.
 */
public class TaskHeadRepositoryTest {

    private static final String TASKHEAD_ID = "123";

    private TaskHeadRepository mTaskHeadsRepository;

    @Mock
    private TaskHeadDataSource mTaskHeadRemoteDataSource;
    @Mock
    private TaskHeadDataSource mTaskHeadLocalDataSource;
    @Mock
    private TaskHeadDataSource.LoadTaskHeadsCallback mLoadTaskHeadsCallback;
    @Captor
    private ArgumentCaptor<TaskHeadDataSource.LoadTaskHeadsCallback> mLoadCallbackCaptor;

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

//    @Test
//    public void editTaskHead_fromLocal_andUpdateCache() {
//        // Save a new taskHead
//        String TITLE = "title_test";
//        TaskHead taskHead = new TaskHead(TITLE, MEMBERS, 0);
//        mTaskHeadsRepository.saveTaskHead(taskHead);
//x
//        // Modify the value of title and members
//        String modifiedTitle = "editTitle";
//        final List<Friend> modifiedMembers = MEMBERS;
//        modifiedMembers.add(new Friend("new member email", "new member name"));
//        // Update the changed members
//        mTaskHeadsRepository.editTaskHead(taskHead.getTaskHeadId(), modifiedTitle, modifiedMembers);
//        verify(mTaskHeadLocalDataSource).editTaskHead(eq(taskHead.getTaskHeadId()), eq(modifiedTitle), eq(modifiedMembers));
//
//        // Update cache
//        assertThat(mTaskHeadsRepository.mCachedTaskHeads.get(taskHead.getTaskHeadId()).getTitle(), is(modifiedTitle));
//    }
//
//    @Test
//    public void addMembers_toLocal_andUpdateCache() {
//        // Save a new taskHead
//        TaskHead taskHead = new TaskHead("title", MEMBERS, 0);
//        mTaskHeadsRepository.saveTaskHead(taskHead);
//
//        // Add members
//        final List<Friend> addedMembers = MEMBERS;
//        addedMembers.add(new Friend("new member email", "new member name"));
//        // Update the added members to repo
//        mTaskHeadsRepository.addMembers(taskHead.getTaskHeadId(), addedMembers);
//        verify(mTaskHeadLocalDataSource).addMembers(eq(taskHead.getTaskHeadId()), eq(addedMembers));
//
//        // update cache
//        assertThat(mTaskHeadsRepository.mCachedTaskHeads.get(taskHead.getTaskHeadId()).getMembers().size(), is(addedMembers.size()));
//    }
//
//    @Test
//    public void deleteTaskHeads_fromCache() {
//        // Save the stubbed taskheads
//        mTaskHeadsRepository.saveTaskHead(TASKHEADS.get(0));
//        mTaskHeadsRepository.saveTaskHead(TASKHEADS.get(1));
//        mTaskHeadsRepository.saveTaskHead(TASKHEADS.get(2));
//
//        // Delete taskHeads TASKHEADS index 1, 2nd
//        List<String> taskheadIds = new ArrayList<>(0);
//        taskheadIds.add(TASKHEADS.get(1).getTaskHeadId());
//        taskheadIds.add(TASKHEADS.get(2).getTaskHeadId());
//        mTaskHeadsRepository.deleteTaskHeads(taskheadIds);
//
//        assertThat(mTaskHeadsRepository.mCachedTaskHeads.size(), is(1));
//        assertThat(mTaskHeadsRepository.mCachedTaskHeads.containsKey(TASKHEADS.get(0).getTaskHeadId()), is(true));
//    }
//
//    @Test
//    public void deleteTaskHeads_fromLocal_andRetrieveTaskHeads() {
//        // Save the stubbed taskheads
//        mTaskHeadsRepository.saveTaskHead(TASKHEADS.get(0));
//        mTaskHeadsRepository.saveTaskHead(TASKHEADS.get(1));
//        mTaskHeadsRepository.saveTaskHead(TASKHEADS.get(2));
//
//        // Delete taskHeads index 0, 1st
//        List<String> taskheadIds = new ArrayList<>(0);
//        taskheadIds.add(TASKHEADS.get(0).getTaskHeadId());
//        taskheadIds.add(TASKHEADS.get(1).getTaskHeadId());
//        mTaskHeadsRepository.deleteTaskHeads(taskheadIds);
//        verify(mTaskHeadLocalDataSource).deleteTaskHeads(taskheadIds);
//
//        // Verify that there is only TASKHEADS.get(2)
//        mTaskHeadsRepository.getTaskHeads(new TaskHeadDataSource.LoadTaskHeadsCallback() {
//            @Override
//            public void onTaskHeadsLoaded(List<TaskHead> taskHeads) {
//                assertThat(taskHeads.get(0).getTitle(), is(TASKHEADS.get(2).getTitle()));
//            }
//
//            @Override
//            public void onDataNotAvailable() {
//                fail();
//            }
//        });
//    }
//
//    @Test
//    public void deleteAllTaskHeads_deleteTaskHeadsFromLocal() {
//        // Save 3 stub taskheads in the repository
//        List<Friend> memebers = MEMBERS = Lists.newArrayList(new Friend("email1", "name1"), new Friend("email2", "name2"),
//                new Friend("email3", "name3"));
//        TaskHead newTaskHead = new TaskHead("title1", memebers, 0);
//        mTaskHeadsRepository.saveTaskHead(newTaskHead);
//        TaskHead newTaskHead2 = new TaskHead("title2", memebers, 1);
//        mTaskHeadsRepository.saveTaskHead(newTaskHead2);
//        TaskHead newTaskHead3 = new TaskHead("title3", memebers, 2);
//        mTaskHeadsRepository.saveTaskHead(newTaskHead3);
//
//        mTaskHeadsRepository.deleteAllTaskHeads();
//
//        verify(mTaskHeadLocalDataSource).deleteAllTaskHeads();
//
//        assertThat(mTaskHeadsRepository.mCachedTaskHeads.size(), is(0));
//    }

    /*
    * convenience methods
    * */
    private void setTaskHeadsNotAvailable(TaskHeadDataSource dataSource) {
        verify(dataSource).getTaskHeads(mLoadCallbackCaptor.capture());
        mLoadCallbackCaptor.getValue().onDataNotAvailable();
    }

    private void setTaskHeadsAvailable(TaskHeadDataSource dataSource, List<TaskHead> taskHeads) {
        verify(dataSource).getTaskHeads(mLoadCallbackCaptor.capture());
        mLoadCallbackCaptor.getValue().onTaskHeadsLoaded(taskHeads);
    }
}