package com.kiwi.auready_ver2.data.source;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.data.TaskHead;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of the in-memory repository with cache.
 */
public class TaskHeadRepositoryTest {

    private static final List<TaskHead> TASKHEADS =
            Lists.newArrayList(new TaskHead("title1"), new TaskHead("title2"), new TaskHead("title3"));

    private TaskHeadRepository mTaskHeadsRepository;

    @Mock
    private TaskHeadDataSource.LoadTaskHeadsCallback mLoadTaskHeadsCallback;
    @Mock
    private TaskHeadDataSource mTaskHeadsRemoteDataSource;
    @Mock
    private TaskHeadDataSource mTaskHeadsLocalDataSource;
    @Captor
    private ArgumentCaptor<TaskHeadDataSource.LoadTaskHeadsCallback> mTaskHeadsCallbackCaptor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mTaskHeadsRepository = TaskHeadRepository.getInstance(
                mTaskHeadsRemoteDataSource, mTaskHeadsLocalDataSource);
    }

    @Test
    public void getTaskHeads_repositoryCachesAfterApiCallToRemoteSource() {
        // Given a setup Captor to capture callbacks
        // When two calls are issued to the taskHeads repository
        twoTaskHeadsLoadCallsToRepository(mLoadTaskHeadsCallback);

        // Then taskHeads were only requested once from Service API.
        verify(mTaskHeadsRemoteDataSource).getTaskHeads(any(TaskHeadDataSource.LoadTaskHeadsCallback.class));
    }

    @Test
    public void getTaskHeads_requestsTaskHeadsFromLocalDataSource() {
        mTaskHeadsRepository.getTaskHeads(mLoadTaskHeadsCallback);

        verify(mTaskHeadsLocalDataSource).getTaskHeads(any(TaskHeadDataSource.LoadTaskHeadsCallback.class));
    }

    @Test
    public void getTaskHeadsWithDirtyCache_taskHeadsAreRetrievedFromRemote() {
        // When calling getTaskHeads in the repository with dirty cache
        mTaskHeadsRepository.refreshTaskHeads();
        mTaskHeadsRepository.getTaskHeads(mLoadTaskHeadsCallback);

        // And the remote data source has data available
        setTaskHeadsAvailable(mTaskHeadsRemoteDataSource, TASKHEADS);

        // Verify the taskHeads from the remote are returned, not the local.
        verify(mTaskHeadsLocalDataSource, never()).getTaskHeads(mLoadTaskHeadsCallback);
        verify(mLoadTaskHeadsCallback).onTaskHeadsLoaded(TASKHEADS);
    }

    @Test
    public void getTaskHeadsWithLocalDataSourceUnavailable_taskHeadsAreRetrievedFromRemote() {
        mTaskHeadsRepository.getTaskHeads(mLoadTaskHeadsCallback);

        // And the local data source has no data available
        setTaskHeadsNotAvailable(mTaskHeadsLocalDataSource);

        // And the remote data source has data available
        setTaskHeadsAvailable(mTaskHeadsRemoteDataSource, TASKHEADS);

        // Verify the taskHeads from the Local are returned.
        verify(mLoadTaskHeadsCallback).onTaskHeadsLoaded(TASKHEADS);
    }

    @Test
    public void getTaskHeadsWithBothDataSourcesUnavailable_firesOnDataUnavailable() {
        mTaskHeadsRepository.getTaskHeads(mLoadTaskHeadsCallback);

        // And the local data source has no data available
        setTaskHeadsNotAvailable(mTaskHeadsLocalDataSource);

        // And the remote data source has no data available
        setTaskHeadsNotAvailable(mTaskHeadsRemoteDataSource);

        // Verify no data is returned
        verify(mLoadTaskHeadsCallback).onDataNotAvailable();
    }

    @Test
    public void getTaskHeads_refreshesLocalDataSource() {
        // Mark cache as dirty to force a reload of data from remote data source.
        // Make the remote data source return data
        // Verify that the data fetched from the remote was saved in local.
    }

    /*
    * Convenience method that issues two calls to the taskHeads repository
    * */
    private void twoTaskHeadsLoadCallsToRepository(TaskHeadDataSource.LoadTaskHeadsCallback callback) {
        // First call to API
        mTaskHeadsRepository.getTaskHeads(callback);

        // Local data source doesn't have data yet.
        verify(mTaskHeadsLocalDataSource).getTaskHeads(mTaskHeadsCallbackCaptor.capture());
        mTaskHeadsCallbackCaptor.getValue().onDataNotAvailable();

        // Then call to Remote data source, trigger callback so taskHeads are cached.
        verify(mTaskHeadsRemoteDataSource).getTaskHeads(mTaskHeadsCallbackCaptor.capture());
        mTaskHeadsCallbackCaptor.getValue().onTaskHeadsLoaded(TASKHEADS);

        // Second call to API
        mTaskHeadsRepository.getTaskHeads(callback);
    }

    private void setTaskHeadsNotAvailable(TaskHeadDataSource dataSource) {
        verify(dataSource).getTaskHeads(mTaskHeadsCallbackCaptor.capture());
        mTaskHeadsCallbackCaptor.getValue().onDataNotAvailable();
    }

    private void setTaskHeadsAvailable(TaskHeadDataSource dataSourcece, List<TaskHead> taskHeads) {
        verify(dataSourcece).getTaskHeads(mTaskHeadsCallbackCaptor.capture());
        mTaskHeadsCallbackCaptor.getValue().onTaskHeadsLoaded(taskHeads);
    }
}