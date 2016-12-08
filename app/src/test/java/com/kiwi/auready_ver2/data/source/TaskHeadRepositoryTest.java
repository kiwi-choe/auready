package com.kiwi.auready_ver2.data.source;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.data.TaskHead;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of the in-memory repository with cache.
 */
public class TaskHeadRepositoryTest {

    // member is one(me)
    private static List<String> MEMBERS = Lists.newArrayList("me");
    private static final List<TaskHead> TASKHEADS =
            Lists.newArrayList(new TaskHead("title1", MEMBERS),
                    new TaskHead("title2", MEMBERS), new TaskHead("title3", MEMBERS));
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
    public void getTaskHeadsWithDirtyCache_taskHeadsAreRetrievedFromRemote() {
        // When calling getTaskHeads in the repo with dirty cache
        mTaskHeadsRepository.refreshTaskHeads();
        mTaskHeadsRepository.getTaskHeads(mLoadTaskHeadsCallback);

        // And the remote data source has data available
        setTaskHeadsAvailable(mTaskHeadRemoteDataSource, TASKHEADS);

        verify(mLoadTaskHeadsCallback).onTaskHeadsLoaded(TASKHEADS);
    }

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

    @Test
    public void getTaskHeads_refreshesLocal() {
        mTaskHeadsRepository.refreshTaskHeads();
        mTaskHeadsRepository.getTaskHeads(mLoadTaskHeadsCallback);

        // Make Remote returns data
        setTaskHeadsAvailable(mTaskHeadRemoteDataSource, TASKHEADS);

        // verify that the data fetched from Remote was saved in Local
        verify(mTaskHeadLocalDataSource, times(TASKHEADS.size())).saveTaskHead(any(TaskHead.class));
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
    public void deleteTaskHead_deleteTaskHeadToServiceApiRemovedFromCache() {
        // 1. Save taskHeads
        TaskHead newTaskHead = new TaskHead();
        mTaskHeadsRepository.saveTaskHead(newTaskHead);
        assertThat(mTaskHeadsRepository.mCachedTaskHeads.containsKey(newTaskHead.getId()), is(true));

        // 2. Delete a taskHead
        mTaskHeadsRepository.deleteTaskHead(newTaskHead.getId());

        // Verify the data sources were called
        verify(mTaskHeadRemoteDataSource).deleteTaskHead(newTaskHead.getId());
        verify(mTaskHeadLocalDataSource).deleteTaskHead(newTaskHead.getId());

        // 3. Verify it's removed from repository
        assertThat(mTaskHeadsRepository.mCachedTaskHeads.containsKey(newTaskHead.getId()), is(false));
    }

    @Test
    public void deleteAllTaskHeads_deleteTaskHeadsFromLocal() {
        // Save 3 stub taskheads in the repository
        List<String> memebers = Lists.newArrayList("mem1");
        TaskHead newTaskHead = new TaskHead("title1", memebers);
        mTaskHeadsRepository.saveTaskHead(newTaskHead);
        TaskHead newTaskHead2 = new TaskHead("title2", memebers);
        mTaskHeadsRepository.saveTaskHead(newTaskHead2);
        TaskHead newTaskHead3 = new TaskHead("title3", memebers);
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