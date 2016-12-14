package com.kiwi.auready_ver2.data.source.local;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.kiwi.auready_ver2.data.source.TaskDataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import static junit.framework.Assert.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Integration test for the {@link TaskDataSource}, which uses the {@link SQLiteDbHelper}.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class TaskLocalDataSourceTest {

    private static final String TASKHEAD_ID = "stub_taskHeadId";
    private static final String MEMBER_ID = "stub_memberId";

    private TaskLocalDataSource mLocalDataSource;

    @Before
    public void setup() {
        mLocalDataSource = TaskLocalDataSource.getInstance(
                InstrumentationRegistry.getTargetContext());
    }

    @After
    public void cleanUp() {
//        mLocalDataSource.deleteTasks();
    }

}
