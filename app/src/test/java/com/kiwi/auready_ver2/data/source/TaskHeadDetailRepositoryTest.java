package com.kiwi.auready_ver2.data.source;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.kiwi.auready_ver2.StubbedData.TaskStub.TASKHEAD_DETAIL;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of the in-memory repository with cache.
 */
public class TaskHeadDetailRepositoryTest {

    private TaskHeadDetailRepository mRepository;

    @Mock
    private TaskHeadDetailDataSource mRemoteDataSource;
    @Mock
    private TaskHeadDetailDataSource mLocalDataSource;
    @Mock
    private TaskHeadDetailDataSource.GetTaskHeadDetailCallback mGetCallback;
    @Mock
    private TaskHeadDetailDataSource.SaveCallback mSaveCallback;
    @Captor
    private ArgumentCaptor<TaskHeadDetailDataSource.GetTaskHeadDetailCallback> mGetCallbackCaptor;
    @Captor
    private ArgumentCaptor<TaskHeadDetailDataSource.SaveCallback> mSaveCallbackCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        mRepository = TaskHeadDetailRepository.getInstance(
                mRemoteDataSource, mLocalDataSource);
    }

    @After
    public void destroyRepositoryInstance() {
        TaskHeadDetailRepository.destroyInstance();
    }

    /*
    * Save taskHeadDetail
    * */
    @Test
    public void saveTaskHead_toCache() {
        // Save a taskHead with members
        mRepository.saveTaskHeadDetail(TASKHEAD_DETAIL, mSaveCallback);
        // Verify that
        assertThat(mRepository.mCachedTaskHeadDetail.getTaskHead().getId(), is(TASKHEAD_DETAIL.getTaskHead().getId()));
        assertThat(mRepository.mCachedMembers.size(), is(TASKHEAD_DETAIL.getMembers().size()));
        assertThat(mRepository.mCachedTaskHeadDetail.getMembers().size(), is(mRepository.mCachedMembers.size()));
    }

    /*
    * Get a TaskHeadDetail
    * */
    @Test
    public void getTaskHeadDetail_fromLocal() {
        String taskHeadId = TASKHEAD_DETAIL.getTaskHead().getId();
        mRepository.getTaskHeadDetail(taskHeadId, mGetCallback);

        verify(mLocalDataSource).getTaskHeadDetail(eq(taskHeadId),
                any(TaskHeadDetailDataSource.GetTaskHeadDetailCallback.class));
    }

    @Test
    public void getTaskHeadDetailWithBothDataSourceUnavailable_firesOnDataUnavailable() {
        String taskHeadId = TASKHEAD_DETAIL.getTaskHead().getId();
        mRepository.getTaskHeadDetail(taskHeadId, mGetCallback);

        setTaskHeadDetailNotAvailable(mLocalDataSource, taskHeadId);
        setTaskHeadDetailNotAvailable(mRemoteDataSource, taskHeadId);

        verify(mGetCallback).onDataNotAvailable();
    }

    /*
    * Delete a TaskHeadDetail
    * */
    @Test
    public void deleteTaskHeadDetail_fromLocal() {
        String taskHeadId = TASKHEAD_DETAIL.getTaskHead().getId();
        mRepository.deleteTaskHeadDetail(taskHeadId);
        verify(mLocalDataSource).deleteTaskHeadDetail(eq(taskHeadId));
    }

    @Test
    public void deleteTaskHeadDetail_fromCacheDetail_andCacheMembers() {
        mRepository.saveTaskHeadDetail(TASKHEAD_DETAIL, mSaveCallback);
        assertThat(mRepository.mCachedTaskHeadDetail, is(notNullValue()));
        mRepository.deleteTaskHeadDetail(TASKHEAD_DETAIL.getTaskHead().getId());
        assertThat(mRepository.mCachedTaskHeadDetail, is(nullValue()));
        assertThat(mRepository.mCachedMembers.size(), is(0));
    }

    private void setTaskHeadDetailNotAvailable(TaskHeadDetailDataSource dataSource, String taskHeadId) {
        verify(dataSource).getTaskHeadDetail(eq(taskHeadId), mGetCallbackCaptor.capture());
        mGetCallbackCaptor.getValue().onDataNotAvailable();
    }


}