package com.kiwi.auready_ver2.tasks;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.when;

/**
 * Unit tests for the implementation of {@link TasksPresenter}.
 */
public class TasksPresenterTest {

    private TasksPresenter mTasksPresenter;

    @Mock
    private TasksContract.View mTasksView;

    @Before
    public void setup() {

        MockitoAnnotations.initMocks(this);

        // The presenter won't update the view unless it's active
        when(mTasksView.isActive()).thenReturn(true);
    }


}